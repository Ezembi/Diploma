package server.app;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import model.data.Data;
import model.data.HttpMetods;
import model.data.Response;
import model.data.SystemParams;
import model.data.VMCommand;
import model.data.VirtualMachine;
import model.io.ServerHandler;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import server.app.exeption.NoArgumentException;
import server.app.exeption.NoFreeVMException;
import server.db.DBManager;
import server.web.HttpRequestHelper;

/**
 *
 * @author user
 */
public class ServerServlet extends HttpServlet {

    private final Logger log = Logger.getLogger("super_server");
    private DataSource ds;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("Server init");
        try {
            ds = (DataSource) new InitialContext().lookup("jdbc/vm");
        } catch (NamingException ex) {
            log.error(ex, ex);
            throw new ServletException(ex);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        long start = System.currentTimeMillis();
        Map<String, String[]> params = request.getParameterMap();
        ServerHandler sh = new ServerHandler(response.getWriter());
        Connection conn = null;
        DBManager db = null;
        log.info("New Request");
        log.info("Params: " + params);
        try {
            conn = ds.getConnection();
            db = new DBManager(conn);
            db.clearDB(
                    Long.parseLong(getSysParam(db.getSystemParams(), SystemParams.LAST_CONNECT)),
                    Long.parseLong(getSysParam(db.getSystemParams(), SystemParams.IP_RESERVE)));
            String method = getParam(params, HttpMetods.METHOD);
            log.info("Execute method: " + method);
            switch (method) {
                case HttpMetods.SEND_JOB:
                    processAddJob(params, sh, db);
                    break;
                case HttpMetods.PING:
                    sh.process(Response.OK);
                    break;
                case HttpMetods.VM_STATE:
                    processVMState(sh, db);
                    break;
                case HttpMetods.SEND_STATE:
                    processVMStateUpdate(params, sh, db);
                    break;
                default:
                    throw new NoArgumentException("Method \"" + method + "\" not supported!");
            }
        } catch (NoArgumentException ex) {
            log.error(ex);
            sh.process(Response.WRONG_METHOD);
        } catch (NoFreeVMException ex) {
            log.error(ex);
            sh.process(Response.VM_NOT_FOUND);
        } catch (SQLException ex) {
            log.error("DB error", ex);
            sh.process(Response.DB_ERROR);
        } catch (Exception ex) {
            log.error("Server exception", ex);
            sh.process(Response.INTERNAL_ERROR);
        } finally {
            log.info("Done, process time: " + (System.currentTimeMillis() - start) + " ms\n\n\n");
            response.getWriter().close();
            DbUtils.closeQuietly(conn);
        }

    }

    /**
     * новая задача на обработку
     *
     * @param params
     * @param sh
     */
    private void processAddJob(Map<String, String[]> params, ServerHandler sh, DBManager db) throws NoArgumentException, SQLException, IOException, NoFreeVMException {
        String data = getParam(params, HttpMetods.DATA);
        log.info("New job: " + data);

        VirtualMachine vm = db.getFistFreeVM();
        if (vm != null) {
            //если есть свободна VM
            sh.process(sh.getProcessedData(HttpRequestHelper.sendingRequest(vm.getIp(), data)));
        } else {

            vm = getMinVM(db.getVMSnapshot());

            if (vm == null) {
                throw new NoFreeVMException("There are no running virtual machines");
            }

            String ip = db.getFreeIP();
            if (!"".equals(ip)) {
                //есть свободный ip
                HttpRequestHelper.sendingStartKillRequest(vm.getIp(), VMCommand.START + "!" + ip);
                log.info("VM ip: " + ip + " starting successfully");
                db.updateIPState(ip, true);
            }
            //нет свободного ip
            vm = db.getFistFreeVM();
            if (vm != null) {
                //если есть свободна VM
                sh.process(sh.getProcessedData(HttpRequestHelper.sendingRequest(vm.getIp(), data)));
            } else {
                vm = getMinVM(db.getVMSnapshot());

                if (vm == null) {
                    throw new NoFreeVMException("There are no running virtual machines");
                }

                sh.process(sh.getProcessedData(HttpRequestHelper.sendingRequest(vm.getIp(), data)));
            }
        }
    }

    private VirtualMachine getMinVM(List<VirtualMachine> snapshot) {
        long minRam = Long.MAX_VALUE;
        int minCpu = Integer.MAX_VALUE;

        VirtualMachine minVM = null;

        for (VirtualMachine vm : snapshot) {
            if (vm.getCpu() <= minCpu && vm.getRam() <= minRam) {
                minVM = vm;
            }
        }

        return minVM;
    }

    /**
     * обновление сост VM
     *
     * @param params
     * @param sh
     * @param db
     * @throws NoArgumentException
     * @throws SQLException
     */
    private void processVMStateUpdate(Map<String, String[]> params, ServerHandler sh, DBManager db) throws NoArgumentException, SQLException {
        String vmStr = getParam(params, HttpMetods.VM);
        log.info("New VM state: " + vmStr);
        VirtualMachine vm = sh.getVirtualMachine(vmStr);

        int freeCount = db.updateVmState(vm);
        sh.process(Response.OK);

        Map<String, String> sParams = db.getSystemParams();

        int maxFreeCount = Integer.parseInt(getSysParam(sParams, SystemParams.FREE_COUNT));
        int minVMCount = Integer.parseInt(getSysParam(sParams, SystemParams.MIN_VM_COUNT));

        if (db.getVMSnapshot().size() > minVMCount) {
            if (freeCount > maxFreeCount) {
                log.info("Try to kill VM: " + vmStr);
                try {
                    HttpRequestHelper.sendingStartKillRequest(vm.getIp(), VMCommand.KILL);
                    db.deleteVM(vm);
                    log.info("VM: " + vmStr + " killing successfully");
                } catch (IOException ex) {
                    log.error("Kill VM error: " + ex, ex);
                }
            }
        }
    }

    /**
     * запрос снапшота VM
     *
     * @param sh
     */
    private void processVMState(ServerHandler sh, DBManager db) throws SQLException {
        log.info("Create vm state list");
        sh.process(db.getVMSnapshot());
    }

    private String getParam(Map<String, String[]> params, String name) throws NoArgumentException {
        String[] data = params.get(name);
        if (data == null || data.length != 1) {
            throw new NoArgumentException("Param \"" + name + "\" is not found");
        }
        String res = data[0];
        if (res == null || res.isEmpty()) {
            throw new NoArgumentException("Param \"" + name + "\" is empty");
        }
        return res;
    }

    private String getSysParam(Map<String, String> params, String name) throws NoArgumentException {
        String data = params.get(name);
        if (data == null) {
            throw new NoArgumentException("Param \"" + name + "\" is not found");
        }
        if (data.isEmpty()) {
            throw new NoArgumentException("Param \"" + name + "\" is empty");
        }
        return data;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("Do Get");
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("Do Post");
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Server component";
    }

}

package server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.data.VirtualMachine;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author user
 */
public class DBManager {

    private static final Logger log = Logger.getLogger("super_server");

    private Connection conn;

    public DBManager(Connection conn) {
        if (conn == null) {
            throw new NullPointerException("Connection can not be null.");
        }
        this.conn = conn;
    }

    /**
     * получить состояния виртуальных машин
     *
     * @return
     * @throws SQLException
     */
    public List<VirtualMachine> getVMSnapshot() throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(
                    "SELECT                                                 \n"
                    + "mv.id_vmachine id,                                   \n"
                    + "mv.cpu cpu,                                          \n"
                    + "mv.ram ram,                                          \n"
                    + "ip.ip ip,                                            \n"
                    + "ip.port port                                         \n"
                    + "  FROM \"VMserver\".ip_pool ip                       \n"
                    + "  JOIN \"VMserver\".vmachine mv using (id_ip)"
            );

            List<VirtualMachine> lvm = new ArrayList<>();

            rs = stmt.executeQuery();
            while (rs.next()) {
                VirtualMachine vm = new VirtualMachine(
                        rs.getInt("id"),
                        rs.getLong("ram"),
                        rs.getInt("cpu"),
                        rs.getString("ip"),
                        rs.getInt("port"));

                lvm.add(vm);
            }
            return lvm;
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
        }
    }
    
    /**
     * очистить БД от умерших машин
     *
     * @param maxVM
     * @param maxIP
     * @throws SQLException
     */
    public void clearDB(long maxVM, long maxIP) throws SQLException {
        
        log.info("Clear DB ...");
        PreparedStatement stmt = null;
        
        try {
            stmt = conn.prepareStatement(
                      "DELETE FROM                              \n"
                    + "\"VMserver\".vmachine vm                 \n"
                    + " WHERE                                   \n" 
                    + createCondition("vm.last_connect", maxVM)
            );
            
            log.info(stmt.executeUpdate() + " VM was removed");
            
            freeIPs(maxIP);
            
            log.info("... DB Clear OK");
        } finally {
            DbUtils.closeQuietly(stmt);
        }
    }
    
    private static String createCondition(String row, long max){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000");
        StringBuilder sb = new StringBuilder();
        sb.append(row).append(" < '").append(df.format(new Date().getTime() - max)).append("'");
        log.info(sb);
        return sb.toString();
    }
    
    public VirtualMachine getFistFreeVM() throws SQLException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(
                     "SELECT                                                \n"
                    + "mv.id_vmachine id,                                   \n"
                    + "mv.cpu cpu,                                          \n"
                    + "mv.ram ram,                                          \n"
                    + "ip.ip ip,                                            \n"
                    + "ip.port port                                         \n"
                    + "  FROM \"VMserver\".ip_pool ip                       \n"
                    + "  JOIN \"VMserver\".vmachine mv using (id_ip)        \n"
                    + "  WHERE mv.free_count>0"
            );
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                log.info("VM with free_count>0 id = " + rs.getInt("id") + " was found");
                return new VirtualMachine(
                        rs.getInt("id"),
                        rs.getLong("ram"),
                        rs.getInt("cpu"),
                        rs.getString("ip"),
                        rs.getInt("port"));
            } else {
                log.info("VM with free_count>0 not found");
                return null;
            }
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pstmt);
        }
    }

    /**
     * регистрация нового ip в БД и возвращение его id в базе
     *
     * @param ip
     * @param port
     * @return 
     * @throws SQLException
     */
    public int registerNewIP(String ip, int port) throws SQLException {
        log.info("Register new ip = " + ip + ":" + port);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(
                    "insert into "
                    + "\"VMserver\".ip_pool("
                    + " ip,"
                    + " port) "
                    + "values "
                    + "(?,?)"
                    + " returning id_ip");

            stmt.setString(1, ip);
            stmt.setInt(2, port);

            stmt.execute();

            rs = stmt.getResultSet();
            rs.next();
            return rs.getInt("id_ip");
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
        }
    }
    
    /**
     * освобождение ip по max времени
     *
     * @param max
     * @throws SQLException
     */
    private void freeIPs(long max) throws SQLException {
        log.info("Free IP ...");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "UPDATE "
                    + "\"VMserver\".ip_pool ip\n"
                    + "   SET "
                    + "reserve=FALSE, "
                    + "last_reserve=now()\n"
                    + " WHERE "
                    + "ip.reserve=TRUE and "
                    + "ip.id_ip not in (SELECT id_ip FROM \"VMserver\".vmachine vm) "
                    + "and " + createCondition("ip.last_reserve", max));

            log.info(stmt.executeUpdate() + " IP was free");
            
            log.info("... IP free OK");
            
        } finally {
            DbUtils.closeQuietly(stmt);
        }
    }
    
    /**
     * зарегстрировать новую VM
     * @param vm
     * @param id_ip 
     */
    private void registerNewVM(VirtualMachine vm, int id_ip) throws SQLException{
        log.info("Register new vm = " + vm + ", with id_ip = " + id_ip);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO                        \n"
                        + "\"VMserver\".vmachine(       \n"
                            + " cpu,                    \n"
                            + " ram,                    \n"
                            + " free_count,             \n"
                            + " id_ip,                  \n"
                            + " last_connect)           \n"
                    + " VALUES (?, ?, 1, ?, now())");

            stmt.setInt(1, vm.getCpu());
            stmt.setLong(2, vm.getRam());
            stmt.setInt(3, id_ip);

            stmt.execute();
            
            updateIPState(id_ip, true);
            
        } finally {
            DbUtils.closeQuietly(stmt);
        }
    }

    /**
     * найти id_ip для ip:port в базе
     * @param ip
     * @param port
     * @return id_ip
     * @throws SQLException 
     */
    private int checkIP(String ip, int port) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(
                    "SELECT                         \n"
                        + "id_ip                    \n"
                    + "FROM                         \n"
                        + "\"VMserver\".ip_pool     \n"
                    + "where                        \n"
                        + "ip=? and                 \n"
                    + " port=?                        "
            );
            
            pstmt.setString(1, ip);
            pstmt.setInt(2, port);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                log.info("Ip = " + ip + ":" + port + " was found");
                return rs.getInt("id_ip");
            } else {
                log.info("Ip = " + ip + ":" + port + " not found");
                return -1;
            }
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pstmt);
        }
    }
    
    /**
     * получить первый свобдный ip
     * @return
     * @throws SQLException 
     */
    public String getFreeIP() throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(
                    "SELECT                     \n"
                    + "id_ip,                   \n"
                    + "ip,                      \n"
                    + "port,                    \n"
                    + "last_reserve             \n"
                    + "  FROM                   \n"
                    + "\"VMserver\".ip_pool ip  \n"
                    + "  WHERE                  \n"
                    + "ip.reserve=FALSE"
            );

            rs = pstmt.executeQuery();
            if (rs.next()) {
                log.info("Free Ip = " + rs.getString("ip") + ":" + rs.getInt("port") + " was found");
                return rs.getString("ip");
            } else {
                log.info("Free Ip not found");
                return "";
            }
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pstmt);
        }
    }
    
    /**
     * обновить состояние блокировки для ip
     * @param id_ip
     * @param newState
     * @throws SQLException 
     */
    private void updateIPState(int id_ip, boolean newState) throws SQLException {
        log.info("Update IP(" + id_ip + ") state = " + newState);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "UPDATE                     \n"
                    + "\"VMserver\".ip_pool     \n"
                    + " SET                     \n"
                    + " reserve=?,              \n"
                    + " last_reserve=now()      \n"
                    + "WHERE                    \n"
                    + " id_ip=" + id_ip);

            stmt.setBoolean(1, newState);

            stmt.execute();
        } finally {
            DbUtils.closeQuietly(stmt);
        }
    }
    
    /**
     * обновить состояние блокировки для ip
     * @param ip
     * @param newState
     * @throws SQLException 
     */
    public void updateIPState(String ip, boolean newState) throws SQLException {
        log.info("Update IP(" + ip + ") state = " + newState);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "UPDATE                     \n"
                    + "\"VMserver\".ip_pool     \n"
                    + " SET                     \n"
                    + " reserve=?,              \n"
                    + " last_reserve=now()      \n"
                    + "WHERE                    \n"
                    + " ip=?");

            stmt.setBoolean(1, newState);
            stmt.setString(2, ip);

            stmt.execute();
        } finally {
            DbUtils.closeQuietly(stmt);
        }
    }
    
    /**
     * найти VM для id_ip в базе
     * @param id_ip
     * @return id_vmachine
     * @throws SQLException 
     */
    private int checkVM(int id_ip) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(
                    "SELECT                         \n"
                        + "id_vmachine              \n"
                    + "FROM                         \n"
                        + "\"VMserver\".vmachine    \n"
                    + "where                        \n"
                        + "id_ip=?                    "
            );
            
            pstmt.setInt(1, id_ip);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                log.info("VM with id_ip = " + id_ip + " was found");
                return rs.getInt("id_vmachine");
            } else {
                log.info("VM with id_ip = " + id_ip + " not found");
                return -1;
            }
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pstmt);
        }
    }
    
    /**
     * обновить инфу о VM
     * @param vm
     * @param id_vm
     * @return 
     */
    private void updateVM(VirtualMachine vm, int id_vm, int freeCount, int id_ip) throws SQLException{

        log.info("Update vm = " + vm + ", with id_vmachine = " + id_vm + " and freeCount = " + freeCount);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "UPDATE \"VMserver\".vmachine   \n"
                    + "SET                          \n"
                        + "cpu=?,                   \n"
                        + "ram=?,                   \n"
                        + "free_count=?,            \n"
                        + "last_connect=now()       \n"
                    + " WHERE id_vmachine=" + id_vm);

            stmt.setInt(1, vm.getCpu());
            stmt.setLong(2, vm.getRam());
            stmt.setInt(3, freeCount);

            stmt.execute();
            
            updateIPState(id_ip, true);
        } finally {
            DbUtils.closeQuietly(stmt);
        }
    }
    
    private int getFreeCount(int id_vm) throws SQLException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(
                    "SELECT                         \n"
                        + "free_count               \n"
                    + "FROM                         \n"
                        + "\"VMserver\".vmachine    \n"
                    + "where                        \n"
                        + "id_vmachine=?                    "
            );
            
            pstmt.setInt(1, id_vm);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                log.info("VM with id_vmachine(free_count) = " + id_vm + " was found");
                return rs.getInt("free_count");
            } else {
                log.info("VM with id_vmachine(free_count) = " + id_vm + " not found");
                return -1;
            }
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pstmt);
        }
    }
    

    /**
     * обновление сосотояния VM
     * 1-проверить ip в базе 
     * 2-если нет - добавить ip и машину 
     * 3-если есть - вытащить id_ip 
     * 4-если нет машины с таким id_ip - добавить (новая) 
     * 5-если есть - провеить загруженность 
     * 6-если загружена - обновить 
     * 7-если нет - вернуть free_count
     *
     * @param vm
     * @return free_count
     */
    public int updateVmState(VirtualMachine vm) throws SQLException {

        //(1)
        int id_ip = checkIP(vm.getIp(), vm.getPort());
        
        if (id_ip > 0) {
            //(3)
            int id_vm = checkVM(id_ip);
            if (id_vm > 0) {
                //(5)
                if (vm.isFree()) {
                    //(7)
                    int freeCount = getFreeCount(id_vm);
                    updateVM(vm, id_vm, freeCount + 1, id_ip);
                    return freeCount + 1;
                } else {
                    //(6)
                    updateVM(vm, id_vm, 0, id_ip);
                    return 0;
                }
            } else {
                //(4)
                registerNewVM(vm, id_ip);
                return 0;
            }
        } else {
            //(2)
            id_ip = registerNewIP(vm.getIp(), vm.getPort());
            registerNewVM(vm, id_ip);
            return 0;
        }
    }
    
    public void deleteVM(VirtualMachine vm) throws SQLException{
        log.info("Delete vm = " + vm);
        int id_ip = checkIP(vm.getIp(), vm.getPort());

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM \"VMserver\".vmachine WHERE id_ip=" + id_ip
            );
            stmt.execute();
            
            updateIPState(id_ip, false);
        } finally {
            DbUtils.closeQuietly(stmt);
        }
    }
    
    public Map<String, String> getSystemParams() throws SQLException {
        Map<String, String> sParams = new HashMap<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(
                    "SELECT key, value FROM \"VMserver\".system_params;"
            );

            rs = pstmt.executeQuery();

            while (rs.next()) {
                sParams.put(rs.getString("key"), rs.getString("value"));
            }
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pstmt);
        }

        return sParams;
    }
}

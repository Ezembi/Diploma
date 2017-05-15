/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.data;

/**
 * систеные параметры
 *
 * @author user
 */
public class SystemParams {

    /**
     * кол-во холостых состояний подряд
     */
    public static final String FREE_COUNT = "free_count";

    /**
     * время с полседнего коннекта vm, после которого её можно ститать мёртвой
     * (в мс)
     */
    public static final String LAST_CONNECT = "last_connect_ms";

    /**
     * время резерва ip без привязки в vm, полсе которого ip можно использовать
     * снова (в мс)
     */
    public static final String IP_RESERVE = "ip_reserve_ms";

    /**
     * max кол-во памяти, после которого машина считается загруженной
     */
    public static final String MAX_RAM = "max_ram";

    /**
     * max загруженность процессора, после которого машина считается загруженной
     */
    public static final String MAX_CPU = "max_cpu";
    
    /**
     * min кол-во запущенных vm
     */
    public static final String MIN_VM_COUNT = "min_vm_count";

}

package com.siat.cn.dai;

import com.siat.cn.dai.base.Requests;
import com.siat.cn.dai.base.Servers;
import com.siat.cn.dai.algorithm.DP;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(final String[] args) {
        Requests rs = new Requests();
        Servers servers = new Servers();
        Map<String, Servers.Server> servers_unit = servers.readServer("data/servers_unit.csv");
        List<Requests.Request> ls = rs.readRequests("data/requests.txt", servers_unit);
        DP dp = new DP(new BigDecimal(1),new BigDecimal(1),ls);
        System.out.println(dp.solution());
        System.out.println();
        //for (re)
    }
}

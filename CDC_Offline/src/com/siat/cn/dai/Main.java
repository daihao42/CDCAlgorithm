package com.siat.cn.dai;

import com.siat.cn.dai.algorithm.Online;
import com.siat.cn.dai.base.Requests;
import com.siat.cn.dai.base.Servers;
import com.siat.cn.dai.algorithm.DP_BAK;
import com.siat.cn.dai.utils.IO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(final String[] args) {
        Requests rs = new Requests();
        Servers servers = new Servers();

        List<String> contents = new ArrayList<>();

        // store results (costs and runtimes) into array
        List<Double> results = new ArrayList<>();

        Map<String, Servers.Server> servers_unit = servers.readServer("data/servers_unit.csv");
        List<Requests.Request> ls = rs.readRequests("data/requests.txt", servers_unit);
        DP_BAK dp = new DP_BAK(new BigDecimal(2),new BigDecimal(4),ls);
        long start1 = System.currentTimeMillis();
        results.add(dp.solution().doubleValue());
        results.add(Double.valueOf(System.currentTimeMillis() - start1));

        List<Requests.Request> ls2 = rs.readRequests("data/requests.txt", servers_unit);
        Online online = new Online(new BigDecimal(2),new BigDecimal(4),ls2,4);
        long start2 = System.currentTimeMillis();
        results.add(online.solution().doubleValue());
        results.add(Double.valueOf(System.currentTimeMillis() - start2));

        contents.add(String.format("%f,%f,%f,%f",results.toArray()));
        contents.add(String.format("%f,%f,%f,%f",results.toArray()));

        IO.writeFile(contents,"data/results.csv");
        System.out.println(results);
        //for (re)
    }
}

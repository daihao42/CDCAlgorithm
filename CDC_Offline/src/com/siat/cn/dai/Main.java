package com.siat.cn.dai;

import com.siat.cn.dai.algorithm.DP;
import com.siat.cn.dai.algorithm.Greedy;
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

        BigDecimal lambda;
        BigDecimal beta = new BigDecimal("0.1");

        Map<String, Servers.Server> servers_unit = servers.readServer("data/servers_unit.csv");
        while(beta.compareTo(new BigDecimal(4)) <= 0) {

            lambda = beta.divide(new BigDecimal(4));

            while((lambda.compareTo(beta.multiply(new BigDecimal(2))) <= 0 )&&(lambda.compareTo(new BigDecimal(2)) <= 0)) {

                System.out.println("start : "+lambda.toString()+" | "+beta.toString());

                // store results (costs and runtimes) into array
                List<Double> results = new ArrayList<>();
                results.add(lambda.doubleValue());
                results.add(beta.doubleValue());

                List<Requests.Request> ls = rs.readRequests("data/requests.txt", servers_unit);
                DP_BAK dp_bak = new DP_BAK(lambda, beta, ls);
                long start1 = System.currentTimeMillis();
                results.add(dp_bak.solution().doubleValue());
                results.add(Double.valueOf(System.currentTimeMillis() - start1));

                List<Requests.Request> ls2 = rs.readRequests("data/requests.txt", servers_unit);
                DP dp = new DP(lambda, beta, ls2);
                long start2 = System.currentTimeMillis();
                results.add(dp.solution().doubleValue());
                results.add(Double.valueOf(System.currentTimeMillis() - start2));

                List<Requests.Request> ls3 = rs.readRequests("data/requests.txt", servers_unit);
                Online online = new Online(lambda, beta, ls3, 4);
                long start3 = System.currentTimeMillis();
                results.add(online.solution().doubleValue());
                results.add(Double.valueOf(System.currentTimeMillis() - start3));

                List<Requests.Request> ls4 = rs.readRequests("data/requests.txt", servers_unit);
                Greedy greedy = new Greedy(lambda, beta, ls4);
                long start4 = System.currentTimeMillis();
                results.add(greedy.solution().doubleValue());
                results.add(Double.valueOf(System.currentTimeMillis() - start4));

                contents.add(String.format("%f,%f,%f,%f,%f,%f", results.toArray()));
                lambda = lambda.add(new BigDecimal("0.01"));
            }
            beta = beta.add(new BigDecimal("0.01"));
        }

        // cost_dp_bak(C & D), runtime_dp_bak(C & D), cost_dp(C & D & T & E), runtime_dp(C & D & T & E), cost_online, runtime_online, cost_greedy, runtime_greedy
        IO.writeFile(contents,"data/results.csv");
        System.out.println();
        //for (re)
    }

    /**
     * test method
     * @param args
     */
    public static void main_bak(String[] args) {
        Requests rs = new Requests();
        Servers servers = new Servers();
        Map<String, Servers.Server> servers_unit = servers.readServer("data/servers_unit.csv");

        BigDecimal lambda = new BigDecimal("0.1");
        BigDecimal beta = new BigDecimal("2");
        List<Double> results = new ArrayList<>();

        List<Requests.Request> ls = rs.readRequests("data/requests.txt", servers_unit);
        DP_BAK dp_bak = new DP_BAK(lambda, beta, ls);
        long start1 = System.currentTimeMillis();
        results.add(dp_bak.solution().doubleValue());
        results.add(Double.valueOf(System.currentTimeMillis() - start1));

        List<Requests.Request> ls2 = rs.readRequests("data/requests.txt", servers_unit);
        DP dp = new DP(lambda, beta, ls2);
        long start2 = System.currentTimeMillis();
        results.add(dp.solution().doubleValue());
        results.add(Double.valueOf(System.currentTimeMillis() - start2));

        List<Requests.Request> ls3 = rs.readRequests("data/requests.txt", servers_unit);
        Online online = new Online(lambda, beta, ls3, 4);
        long start3 = System.currentTimeMillis();
        results.add(online.solution().doubleValue());
        results.add(Double.valueOf(System.currentTimeMillis() - start3));

        List<Requests.Request> ls4 = rs.readRequests("data/requests.txt", servers_unit);
        Greedy greedy = new Greedy(lambda, beta, ls4);
        long start4 = System.currentTimeMillis();
        results.add(greedy.solution().doubleValue());
        results.add(Double.valueOf(System.currentTimeMillis() - start4));

        System.out.println(results);

        /**
        BigDecimal t = new BigDecimal(3.1);
        BigDecimal a = new BigDecimal(1.6646);
        System.out.println(t.divide(a,4,BigDecimal.ROUND_UP));
         **/
    }
}

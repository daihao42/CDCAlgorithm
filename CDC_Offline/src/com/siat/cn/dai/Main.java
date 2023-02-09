package com.siat.cn.dai;

import com.siat.cn.dai.algorithm.DP;
import com.siat.cn.dai.algorithm.Greedy;
import com.siat.cn.dai.algorithm.MCAO;
import com.siat.cn.dai.algorithm.Online;
import com.siat.cn.dai.algorithm.OnlineThree;
import com.siat.cn.dai.algorithm.OnlineUpdate;
import com.siat.cn.dai.base.Requests;
import com.siat.cn.dai.base.Servers;
import com.siat.cn.dai.algorithm.DPBAK;
import com.siat.cn.dai.utils.IO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main_t(final String[] args) {
        Requests rs = new Requests();
        Servers servers = new Servers();

        List<String> contents = new ArrayList<>();

        BigDecimal lambda;
        BigDecimal beta = new BigDecimal("0.1");

        Map<String, Servers.Server> servers_unit = servers.readServer("data/server_units/servers_unit_1_3.csv");
        while(beta.compareTo(new BigDecimal(4)) <= 0) {
        //while(beta.compareTo(new BigDecimal(0.1)) <= 0) {

            lambda = beta.divide(new BigDecimal(5));

            while((lambda.compareTo(beta.multiply(new BigDecimal(2))) <= 0 )&&(lambda.compareTo(new BigDecimal(2)) <= 0)) {

                System.out.println("start : "+lambda.toString()+" | "+beta.toString());

                // store results (costs and runtimes) into array
                List<Double> results = new ArrayList<>();
                results.add(lambda.doubleValue());
                results.add(beta.doubleValue());

                List<Requests.Request> ls = rs.readRequests("data/requests.txt", servers_unit);
                DPBAK dp_bak = new DPBAK(lambda, beta, ls);
                long start1 = System.currentTimeMillis();
                results.add(dp_bak.solution().doubleValue());
                results.add(Double.valueOf(System.currentTimeMillis() - start1));

                List<Requests.Request> ls2 = rs.readRequests("data/requests.txt", servers_unit);
                DP dp = new DP(lambda, beta, ls2);
                long start2 = System.currentTimeMillis();
                results.add(dp.solution().doubleValue());
                results.add(Double.valueOf(System.currentTimeMillis() - start2));

                List<Requests.Request> ls3 = rs.readRequests("data/requests.txt", servers_unit);
                Online online = new Online(lambda, beta, ls3, 1);
                long start3 = System.currentTimeMillis();
                results.add(online.solution().doubleValue());
                results.add(Double.valueOf(System.currentTimeMillis() - start3));

                List<Requests.Request> ls4 = rs.readRequests("data/requests.txt", servers_unit);
                Greedy greedy = new Greedy(lambda, beta, ls4);
                long start4 = System.currentTimeMillis();
                results.add(greedy.solution().doubleValue());
                results.add(Double.valueOf(System.currentTimeMillis() - start4));

                contents.add(String.format("%f,%f,%f,%f,%f,%f,%f,%f,%f,%f", results.toArray()));
                lambda = lambda.add(new BigDecimal("0.01"));
            }
            beta = beta.add(new BigDecimal("0.01"));
        }

        // cost_dp_bak(C & D), runtime_dp_bak(C & D), cost_dp(C & D & T & E), runtime_dp(C & D & T & E), cost_online, runtime_online, cost_greedy, runtime_greedy
        IO.writeFile(contents,"data/results.csv");
        System.out.println();
        //for (re)
    }



    public static void main20210728(final String[] args) {
        Requests rs = new Requests();
        Servers servers = new Servers();

        List<String> contents = new ArrayList<>();

        Map<String, Servers.Server> servers_unit = servers.readServer("./data/servers_unit_0.4_1.6.csv");

        for(float modelSize = 100; modelSize <= 1000; modelSize += 50) {
        //for(float modelSize = 100; modelSize <= 100; modelSize += 50) {

            BigDecimal lambda = new BigDecimal(modelSize*(100.0/1000)).setScale(1,BigDecimal.ROUND_HALF_UP);
            BigDecimal beta = new BigDecimal(modelSize*(400.0/1000)).setScale(1,BigDecimal.ROUND_HALF_UP);

            System.out.println("start : " + lambda.toString() + " | " + beta.toString());

            // store results (costs and runtimes) into array
            List<Double> results = new ArrayList<>();
            results.add(Double.valueOf(modelSize));
            results.add(lambda.doubleValue());
            results.add(beta.doubleValue());

            List<Requests.Request> ls2 = rs.readRequests("data/requests.txt", servers_unit);
            DP dp = new DP(lambda, beta, ls2);
            long start2 = System.currentTimeMillis();
            results.add(dp.solution().doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start2));


            /**
            List<Requests.Request> ls4 = rs.readRequestsWithSample("data/requests.txt", servers_unit,samplesize);
            Greedy greedy = new Greedy(lambda, beta, ls4);
            long start4 = System.currentTimeMillis();
            results.add(greedy.solution().doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start4));
            **/

            List<Requests.Request> ls3 = rs.readRequests("data/requests.txt", servers_unit);
            Online online = new Online(lambda, beta, ls3, 1);
            long start3 = System.currentTimeMillis();
            results.add(online.solution().doubleValue());
            results.add(online.cache_cost.doubleValue());
            results.add(online.transfer_cost.doubleValue());
            results.add(online.pull_cost.doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start3));

            //modelVersion
            List<String> onlinemodelversion = new ArrayList<>();
            for(Map.Entry<Integer,Integer> entry: online.modelVersion.entrySet()){
                onlinemodelversion.add(entry.getKey().toString()+","+entry.getValue().toString());
            }
            IO.writeFile(onlinemodelversion, "data/onlineModelVersion.csv");


            List<Requests.Request> ls4 = rs.readRequests("data/requests.txt", servers_unit); OnlineThree online3 = new OnlineThree(lambda, beta, ls4, 1);
            long start4 = System.currentTimeMillis();
            results.add(online3.solution().doubleValue());
            results.add(online3.cache_cost.doubleValue());
            results.add(online3.transfer_cost.doubleValue());
            results.add(online3.pull_cost.doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start4));

            //modelVersion
            List<String> online3modelversion = new ArrayList<>();
            for(Map.Entry<Integer,Integer> entry: online3.modelVersion.entrySet()){
                online3modelversion.add(entry.getKey().toString()+","+entry.getValue().toString());
            }
            IO.writeFile(online3modelversion, "data/online3ModelVersion.csv");


            List<Requests.Request> ls5 = rs.readRequests("data/requests.txt", servers_unit);
            OnlineUpdate onlineupdate = new OnlineUpdate(lambda, beta, ls5, 1);
            long start5 = System.currentTimeMillis();
            results.add(onlineupdate.solution().doubleValue());
            results.add(onlineupdate.cache_cost.doubleValue());
            results.add(onlineupdate.transfer_cost.doubleValue());
            results.add(onlineupdate.pull_cost.doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start5));

            List<Requests.Request> ls6 = rs.readRequests("data/requests.txt", servers_unit);
            MCAO mcao = new MCAO(lambda, ls6, servers_unit);
            long start6 = System.currentTimeMillis();
            results.add(mcao.solution().doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start6));

            contents.add(String.format("%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f", results.toArray()));
            System.out.println(results);
        }

        IO.writeFile(contents,"data/online_results_2021_07.csv");
        //System.out.println();
        //for (re)
    }

    public static void main(final String[] args) {
        Requests rs = new Requests();
        Servers servers = new Servers();

        List<String> contents = new ArrayList<>();

        Map<String, Servers.Server> servers_unit = servers.readServer("./data/servers_unit_0.4_1.6.csv");

        /** 
        for(float modelSize = 50; modelSize <= 300; modelSize += 50) {

            BigDecimal lambda = new BigDecimal(modelSize*(6.0/1000)).setScale(1,BigDecimal.ROUND_HALF_UP);
            BigDecimal beta = new BigDecimal(modelSize*(14.0/1000)).setScale(1,BigDecimal.ROUND_HALF_UP);

            System.out.println("start : " + lambda.toString() + " | " + beta.toString());

            // store results (costs and runtimes) into array
            List<Double> results = new ArrayList<>();
            results.add(Double.valueOf(modelSize));
            results.add(lambda.doubleValue());
            results.add(beta.doubleValue());

            List<Requests.Request> ls2 = rs.readRequests("data/requests.txt", servers_unit);
            DP dp = new DP(lambda, beta, ls2);
            long start2 = System.currentTimeMillis();
            results.add(dp.solution().doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start2));

            List<Requests.Request> ls4 = rs.readRequests("data/requests.txt", servers_unit);
            Greedy greedy = new Greedy(lambda, beta, ls4);
            long start4 = System.currentTimeMillis();
            results.add(greedy.solution().doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start4));

            contents.add(String.format("%f,%f,%f,%f,%f,%f,%f", results.toArray()));
            System.out.println(results);
        }

        IO.writeFile(contents,"data/greedy_pr_2021_03.csv");
        System.out.println();
        //for (re)
        **/

        contents.clear();
        for(int samplesize = 100; samplesize <= 7000; samplesize += 200) {

            BigDecimal lambda = new BigDecimal(100*(6.0/1000)).setScale(1,BigDecimal.ROUND_HALF_UP);
            BigDecimal beta = new BigDecimal(100*(14.0/1000)).setScale(1,BigDecimal.ROUND_HALF_UP);

            System.out.println("start : " + lambda.toString() + " | " + beta.toString());

            // store results (costs and runtimes) into array
            List<Double> results = new ArrayList<>();
            results.add(Double.valueOf(samplesize));
            results.add(lambda.doubleValue());
            results.add(beta.doubleValue());

            List<Requests.Request> ls2 = rs.readRequestsWithSample("data/requests.txt", servers_unit,samplesize);
            DP dp = new DP(lambda, beta, ls2);
            long start2 = System.currentTimeMillis();
            results.add(dp.solution().doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start2));

            Greedy greedy = new Greedy(lambda, beta, ls2);
            long start4 = System.currentTimeMillis();
            results.add(greedy.solution().doubleValue());
            results.add(Double.valueOf(System.currentTimeMillis() - start4));

            contents.add(String.format("%f,%f,%f,%f,%f,%f,%f", results.toArray()));
            System.out.println(results);
        }

        IO.writeFile(contents,"data/greedy_sample_2021_07.csv");

    }


    public static String[] generateBetaArr(String clambda){
        String[] beta_arr = new String[3];
        beta_arr[0] = (Integer.valueOf(clambda) -1)+"";
        beta_arr[1] = (Integer.valueOf(clambda)*2 - 1)+"";
        beta_arr[2] = (Integer.valueOf(clambda)*2 + 1)+"";
        return beta_arr;
    }
    /**
     * test method
     * @param args
     */
    public static void main_t2(String[] args) {
        Requests rs = new Requests();
        Servers servers = new Servers();

        String[] lambda_arr = {"2","4","6"};

        List<String> contents = new ArrayList<>();

        for(String lambda_str : lambda_arr) {
            String[] beta_arr = generateBetaArr(lambda_str);
            for (String beta_str : beta_arr) {

                BigDecimal lambda = new BigDecimal(lambda_str);
                BigDecimal beta = new BigDecimal(beta_str);

                for(int low = 1;low<5;low++) {
                    for(int high = low;high < low+5;high++) {

                        Map<String, Servers.Server> servers_unit = servers.readServer(String.format("data/server_units/servers_unit_%d_%d.csv",low,high));

                        List<Double> results = new ArrayList<>();

                        results.add(lambda.doubleValue());
                        results.add(beta.doubleValue());
                        results.add(Double.valueOf(low));
                        results.add(Double.valueOf(high));

                        List<Requests.Request> ls = rs.readRequests("data/requests.txt", servers_unit);
                        DPBAK dp_bak = new DPBAK(lambda, beta, ls);
                        long start1 = System.currentTimeMillis();
                        results.add(dp_bak.solution().doubleValue());
                        results.add(Double.valueOf(System.currentTimeMillis() - start1));

                        List<Requests.Request> ls2 = rs.readRequests("data/requests.txt", servers_unit);
                        DP dp = new DP(lambda, beta, ls2);
                        long start2 = System.currentTimeMillis();
                        results.add(dp.solution().doubleValue());
                        results.add(Double.valueOf(System.currentTimeMillis() - start2));

                        List<Requests.Request> ls3 = rs.readRequests("data/requests.txt", servers_unit);
                        Online online = new Online(lambda, beta, ls3, 1);
                        long start3 = System.currentTimeMillis();
                        results.add(online.solution().doubleValue());
                        results.add(Double.valueOf(System.currentTimeMillis() - start3));

                        List<Requests.Request> ls4 = rs.readRequests("data/requests.txt", servers_unit);
                        Greedy greedy = new Greedy(lambda, beta, ls4);
                        long start4 = System.currentTimeMillis();
                        results.add(greedy.solution().doubleValue());
                        results.add(Double.valueOf(System.currentTimeMillis() - start4));

                        System.out.println(results);

                        contents.add(String.format("%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f", results.toArray()));
                    }
                }
            }
        }

        IO.writeFile(contents,"data/results.csv");
    }
}

package com.siat.cn.dai.algorithm;

import com.siat.cn.dai.base.Requests.Request;
import com.siat.cn.dai.base.Servers;

import java.math.BigDecimal;
import java.util.*;

//Minimal Cost-Unit Server Always Online
public class MCAO {

    private BigDecimal lambda;

    private List<Request> request_list;

    private Map<String, Servers.Server> server_unit;

    private BigDecimal minimal_cost_unit;

    private Servers.Server minimal_cost_unit_server;

    private BigDecimal max_time;

    private int not_on_min_cost_unit_number;

    public MCAO(BigDecimal lambda, List<Request> request_list, Map<String,Servers.Server> server_unit){
        this.lambda = lambda;
        this.request_list = request_list;
        this.server_unit = server_unit;
        findMaxTime();
        findMinimalCostUnit();
        findRequestNumberNotOnMininalCostUnitServer();
    }

    public void findMaxTime(){
        BigDecimal max_time = new BigDecimal(0);
        for(Request r:this.request_list){
            max_time = max_time.max(r.getTime());
        }
        this.max_time = max_time;
    }

    public void findRequestNumberNotOnMininalCostUnitServer(){
        int not_on_min_cost_unit_number = 0;
        for(Request r: this.request_list){
            if(!r.getServerid().equals(this.minimal_cost_unit_server.sid)){
                not_on_min_cost_unit_number++;
            }
        }
        this.not_on_min_cost_unit_number = not_on_min_cost_unit_number;
    }

    public void findMinimalCostUnit(){
        BigDecimal minimal_cost_unit = new BigDecimal(Double.MAX_VALUE); 
        Servers.Server minimal_cost_unit_server = null;
        for(Servers.Server s: this.server_unit.values()){
            if(s.getCostUnit().compareTo(minimal_cost_unit) < 0){
                minimal_cost_unit = s.getCostUnit();
                minimal_cost_unit_server = s;
            }
        }
        this.minimal_cost_unit = minimal_cost_unit;
        this.minimal_cost_unit_server = minimal_cost_unit_server;
    }
            
    public BigDecimal solution(){
        BigDecimal totalCost = this.max_time.multiply(this.minimal_cost_unit).add(
            this.lambda.multiply(new BigDecimal(this.not_on_min_cost_unit_number))
        );
        return totalCost;
    }
}

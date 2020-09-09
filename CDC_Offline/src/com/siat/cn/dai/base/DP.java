package com.siat.cn.dai.base;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DP {

    private BigDecimal lambda;
    private BigDecimal beta;

    private List<BigDecimal> costBoundB = new ArrayList<>();
    private List<BigDecimal> semiOptD = new ArrayList<>();
    private List<BigDecimal> optC = new ArrayList<>();

    public DP(BigDecimal lambda,BigDecimal beta){
        this.lambda = lambda;
        this.beta = beta;
    }

    public BigDecimal CachingCost(BigDecimal mu,BigDecimal delta_t){
        return mu.multiply(delta_t);
    }

    public BigDecimal TransferringCost(){
        return lambda;
    }

    public BigDecimal UploadingCost(){
        return beta;
    }

    public BigDecimal getLambda() {
        return lambda;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public List<BigDecimal> getCostBoundB() {
        return costBoundB;
    }

    public List<BigDecimal> getSemiOptD() {
        return semiOptD;
    }

    public List<BigDecimal> getOptC() {
        return optC;
    }

    public List<BigDecimal> calc_CostBoundB(List<Requests.Request> requests){
        List<BigDecimal> res = new ArrayList<>();
        res.add(new BigDecimal(0));
        Map<String, Requests.Request> lastReq = new HashMap<String, Requests.Request>();
        for(Requests.Request i : requests){
            if(lastReq.get(i.serverid) == null){
                res.add(this.TransferringCost());
            }
            else {
                BigDecimal caching_cost = CachingCost(i.getServer().cost_unit,i.getTime().subtract(lastReq.get(i.serverid).time));
                res.add(caching_cost.compareTo(TransferringCost()) >= 1 ? caching_cost:TransferringCost());
            }
        }
        return res;
    }

    public BigDecimal calc_SemiOptD(){

        return null;
    }

    public BigDecimal calc_OptC(){
        return null;
    }

    public BigDecimal solution(){

        return null;
    }
}

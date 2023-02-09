package com.siat.cn.dai.algorithm;

import com.siat.cn.dai.base.Requests;
import com.siat.cn.dai.base.Servers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineThree {
    private BigDecimal lambda;
    private BigDecimal beta;
    private BigDecimal time_unit;
    private int precision;

    private List<Requests.Request> request_list;

    private int cached_servers = 0;
    private List<CaseServer> cachedServerList = new ArrayList<>();

    private BigDecimal total_cost = new BigDecimal(0);

    private int request_index = 0;

    private int model_index = 0;

    public Map<Integer,Integer> modelVersion = new HashMap<>();

    private int cache_counter = 0;

    public BigDecimal cache_cost = new BigDecimal(0);

    private int transfer_counter = 0;

    public BigDecimal transfer_cost = new BigDecimal(0);

    private int pull_counter = 0;

    public BigDecimal pull_cost = new BigDecimal(0);

    public void addCacheCounter(BigDecimal cost){
        cache_cost = cache_cost.add(cost);
        cache_counter++;
    }

    public void addTransferCounter(BigDecimal cost){
        transfer_cost = transfer_cost.add(cost);
        transfer_counter++;
    }

    public void addPullCounter(BigDecimal cost){
        pull_cost = pull_cost.add(cost);
        pull_counter++;
    }

    public void checkModelVersion(boolean updateflag){
        if(updateflag){
            model_index = (int)(request_index/500);
        }
        this.modelVersion.put(request_index+1, model_index);
    }


    public OnlineThree(BigDecimal lambda, BigDecimal beta, List<Requests.Request> request_list, int precision){
        this.lambda = lambda;
        this.beta = beta;
        this.request_list = request_list;
        this.precision = precision;
    }

    private BigDecimal startTime(){
        BigDecimal min_time = new BigDecimal(Double.MAX_VALUE);
        for(Requests.Request r:request_list){
            min_time = min_time.min(r.getTime());
        }
        return min_time;
    }

    public CaseServer getMinCostUnitServerFormActiveServer(){
        CaseServer mincost = null;
        for (CaseServer cs: this.cachedServerList) {
            if (mincost == null){
                mincost = cs;
            } else if(cs.server.getCostUnit().compareTo(mincost.server.getCostUnit()) < 0){
                mincost = cs;
            }
        }
        return mincost;
    }

    public BigDecimal solution(){
        Map<String,List<Requests.Request>> request_map = this.getMapServer();
        BigDecimal time_range = this.TimeRange();

        // time unit for runner
        String s_P = "0.";
        for(int i=1;i<this.precision;i++){
            s_P+="0";
        }
        s_P += "1";
        this.time_unit = new BigDecimal(s_P);

        // choose case for solution
        //List<CaseServer> caseservers = new ArrayList<>();
        //special for set min_cost_server in case 3
        List<Case3Server> case3servers = new ArrayList<>();
        for (Map.Entry<String,List<Requests.Request>> entry : request_map.entrySet()) {
            case3servers.add(new Case3Server(entry.getValue(), this));
        }

        //startTime
        BigDecimal start_time = startTime();
        for (BigDecimal itime = start_time;itime.compareTo(time_range) <= 0;itime = itime.add(this.time_unit)){
            for(Case3Server cs:case3servers) {
                this.request_index = cs.Notify(itime, this.request_index);
            }
        }
        return this.total_cost;
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

    public void addCachedServer(CaseServer cs){
        this.cachedServerList.add(cs);
        this.cached_servers++;
    }

    public void minusCachedServer(CaseServer cs){
        this.cached_servers--;
        this.cachedServerList.remove(cs);
        if(this.cached_servers < 0){
            System.out.println("cached_server become minus!");
        }
    }

    public void addCost(BigDecimal c){
        this.total_cost = this.total_cost.add(c);
    }

    public BigDecimal TimeRange(){
        BigDecimal min_unit = new BigDecimal(Double.MAX_VALUE);
        BigDecimal max_time = new BigDecimal(0);
        for(Requests.Request r:request_list){
            min_unit = min_unit.min(r.getServer().getCostUnit());
            max_time = max_time.max(r.getTime());
        }
        return max_time.add(
                this.beta.add(this.lambda).multiply(new BigDecimal(2)).divide(min_unit,precision,BigDecimal.ROUND_UP)
        );
    }

    private Map<String, List<Requests.Request>> getMapServer(){
        Map<String, List<Requests.Request>> res = new HashMap<>();
        for(Requests.Request r: this.request_list){
            if(res.get(r.getServerid()) == null){
                res.put(r.getServerid(),new ArrayList<Requests.Request>());
                res.get(r.getServerid()).add(r);
            }
            else{
                res.get(r.getServerid()).add(r);
            }
        }
        return res;
    }

    /**
     * for three cases, each case has its own solution method -- Notify(itime)
     */
    abstract class CaseServer{
        public List<Requests.Request> requestList;
        public BigDecimal expire_time = new BigDecimal(0);
        public BigDecimal last_request_time = new BigDecimal(0);
        public Boolean cached = Boolean.FALSE;
        public Servers.Server server;
        public OnlineThree OnlineSolution;
        public CaseServer(List<Requests.Request> requestList, OnlineThree onlinesolution){
            this.requestList = requestList;
            this.OnlineSolution = onlinesolution;
            this.server = requestList.get(0).getServer();
        }

        public Boolean meetRequest(BigDecimal itime){
            if((this.requestList.size()!=0)&&(this.requestList.get(0).getTime().compareTo(itime) <= 0)){
                this.requestList.remove(0);
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        public void TransCacheToOthers(BigDecimal itime){
            BigDecimal cost = CachingCost(this.server.getCostUnit(),itime.subtract(this.last_request_time));
            addCost(cost);
            this.last_request_time = itime;
            this.expire_time = TransferringCost().divide(this.server.getCostUnit(),precision,BigDecimal.ROUND_UP);
            this.cached = Boolean.TRUE;
            this.OnlineSolution.addCacheCounter(cost);
        }

        public abstract int Notify(BigDecimal itime, int request_index);

        public abstract void Delete(BigDecimal itime);
    }

    class Case3Server extends CaseServer{

        public Case3Server(List<Requests.Request> requestList, OnlineThree online){
            super(requestList, online);
        }

        private Case3Server minCostUnitServer;

        public void setMinCostUnitServer(Case3Server cs){
            this.minCostUnitServer = cs;
        }

        @Override
        public int Notify(BigDecimal itime, int request_index) {
            BigDecimal cost;
            boolean updateflag = false;
            if (this.meetRequest(itime)){
                request_index++;
                if (this.cached){
                    cost = CachingCost(this.server.getCostUnit(),itime.subtract(this.last_request_time));
                    this.OnlineSolution.addCacheCounter(cost);
                }else if(cached_servers > 0){
                    cost = TransferringCost();
                    addCachedServer(this);

                    /** 
                    // notify the server who transferred cache to here, and extend its expire_time
                    CaseServer minCachedServer = getMinCostUnitServerFormActiveServer();
                    if(minCachedServer.server.getCostUnit().compareTo(this.server.getCostUnit()) < 0) {
                        getMinCostUnitServerFormActiveServer().TransCacheToOthers(itime);
                    }
                    **/

                    this.OnlineSolution.addTransferCounter(cost);
                }
                else{
                    cost = UploadingCost();
                    addCachedServer(this);
                    this.OnlineSolution.addPullCounter(cost);

                    updateflag = true;
                }
                addCost(cost);
                this.last_request_time = itime;
                this.expire_time = TransferringCost().divide(this.server.getCostUnit(),precision,BigDecimal.ROUND_UP);
                this.cached = Boolean.TRUE;

                this.OnlineSolution.checkModelVersion(updateflag);
            }
            if (this.expire_time.compareTo(time_unit) > 0) {
                this.expire_time = this.expire_time.subtract(time_unit);
                if (this.expire_time.compareTo(time_unit) <= 0) {
                    this.Delete(itime);
                }
            }

            return request_index;
        }

        @Override
        public void Delete(BigDecimal itime) {
            //System.out.println("call 3 delete"+itime);
            BigDecimal cost;

            if(cached_servers == 1){
                cost = TransferringCost();
                addCost(cost);
                this.OnlineSolution.addTransferCounter(cost);
            } 
            cost = CachingCost(this.server.getCostUnit(), itime.subtract(this.last_request_time));
            addCost(cost);

            this.cached = Boolean.FALSE;
            minusCachedServer(this);
            this.expire_time = new BigDecimal(0);

            this.OnlineSolution.addCacheCounter(cost);
        }
    }

}

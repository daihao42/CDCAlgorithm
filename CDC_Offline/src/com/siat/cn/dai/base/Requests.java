package com.siat.cn.dai.base;

import com.siat.cn.dai.utils.IO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Requests {
    public class Request{
        String serverid;
        BigDecimal time;
        Servers.Server server;
        public Request(String time, Servers.Server server){
            this.server = server;
            this.serverid = server.sid;
            this.time = new BigDecimal(time);
        }

        public BigDecimal getTime() {
            return time;
        }

        public Servers.Server getServer(){
            return server;
        }

        public String getServerid() {
            return serverid;
        }
    }

    public List<Request> readRequests(String requestfile, Map<String, Servers.Server> serverMap){
        List<String> lines = IO.readFileByLine(requestfile);
        List<Request> lr = new ArrayList<>();
        for(String line : lines){
            String[] ls = line.split(",");
            lr.add(new Request(ls[0],serverMap.get(ls[1])));
        }
        lr.sort(new Comparator<Request>() {
            @Override
            public int compare(Requests.Request o0, Requests.Request o2) {
                return o0.getTime().compareTo(o2.getTime());
            }
        });
        return lr;
    }
}

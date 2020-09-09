import pandas as pd
import pylab as plt
import seaborn as sns
import random
from collections import defaultdict

df = pd.read_csv("requests.txt",header=None)
servers = df[0].drop_duplicates()

# 生成呈正态分布的随机数
# print("normalvariate: ", random.normalvariate(0, 1))

# 产生一组满足正太分布的随机数
walk = []

def randomUnit(servers,low,high):
    random.seed(1)
    res = defaultdict()
    for i in servers:
        res[i] = round(random.uniform(low,high),2)
    return res
server_unit = randomUnit(servers.tolist(),1,5)
#pd.DataFrame(dict(server_unit),index=[0]).T.to_csv("servers_unit.csv",header=None,index=None)
pd.DataFrame(dict(server_unit),index=[0]).T.to_csv("servers_unit.csv",header=None)

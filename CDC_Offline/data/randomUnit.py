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
    random.seed(3)
    res = defaultdict()
    for i in servers:
        res[i] = round(random.uniform(low,high),2)
    return res

'''
for low in range(1,5):
    for high in range(low,low+5):
        server_unit = randomUnit(servers.tolist(),low,high)

        pd.DataFrame(dict(server_unit),index=[0]).T.to_csv("servers_unit_{}_{}.csv".format(low,high),header=None)
#test = pd.DataFrame(dict(server_unit),index=[0]).T
#test[0] = 2
#test.loc[min(test.index)] = 1
#test.to_csv("servers_unit.csv",header=None)
'''

server_unit = randomUnit(servers.tolist(),0.8,1.6)
pd.DataFrame(dict(server_unit),index=[0]).T.to_csv("servers_unit_{}_{}.csv".format(0.4,1.6),header=None)

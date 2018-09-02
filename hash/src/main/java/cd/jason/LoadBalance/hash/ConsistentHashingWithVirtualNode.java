/**    
 * 文件名：ConsistentHashingWithVirtualNode.java    
 *    
 * 版本信息：    
 * 日期：2018年9月3日    
 * Copyright 足下 Corporation 2018     
 * 版权所有    
 *    
 */
package cd.jason.LoadBalance.hash;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**    
 *     
 * 项目名称：hash    
 * 类名称：ConsistentHashingWithVirtualNode    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2018年9月3日 上午1:14:16    
 * 修改人：jinyu    
 * 修改时间：2018年9月3日 上午1:14:16    
 * 修改备注：    
 * @version     
 *     
 */
public class ConsistentHashingWithVirtualNode {
        /**
         * 待添加入Hash环的服务器列表
         */
        private  String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
                "192.168.0.3:111", "192.168.0.4:111"};
        private List<String> lstServers=new ArrayList<String>(3);
        /**
         * 真实结点列表,考虑到服务器上线、下线的场景，即添加、删除的场景会比较频繁，这里使用LinkedList会更好
         */
        private  List<String> realNodes = new LinkedList<String>();
        
        /**
         * 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
         */
        private  SortedMap<Integer, String> virtualNodes = 
                new TreeMap<Integer, String>();
        
        /**
         * 虚拟节点的数目，这里写死，为了演示需要，一个真实结点对应5个虚拟节点
         */
        private  final int VIRTUAL_NODES = 10;
        private  boolean isInit=true;//需要初始化
        private void init()
        {
            realNodes.clear();
            virtualNodes.clear();
            servers=new String[lstServers.size()];
            lstServers.toArray(servers);
            // 先把原始的服务器添加到真实结点列表中
            for (int i = 0; i < servers.length; i++)
                realNodes.add(servers[i]);
            // 再添加虚拟节点，遍历LinkedList使用foreach循环效率会比较高
            for (String str : realNodes)
            {
                for (int i = 0; i < VIRTUAL_NODES; i++)
                {
                    String virtualNodeName = str + "&&VN" + String.valueOf(i);
                    int hash = getHash(virtualNodeName);
                    System.out.println("虚拟节点[" + virtualNodeName + "]被添加, hash值为" + hash);
                    virtualNodes.put(hash, virtualNodeName);
                }
            }
        }
        
        /**
         * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别 
         */
        private  int getHash(String str)
        {
            final int p = 16777619;
            int hash = (int)2166136261L;
            for (int i = 0; i < str.length(); i++)
                hash = (hash ^ str.charAt(i)) * p;
            hash += hash << 13;
            hash ^= hash >> 7;
            hash += hash << 3;
            hash ^= hash >> 17;
            hash += hash << 5;
            
            // 如果算出来的值为负数则取其绝对值
            if (hash < 0)
                hash = Math.abs(hash);
            return hash;
        }
        
        /**
         * 
        * @Title: addServeNode
        * @Description: 添加节点
        * @param @param node    参数
        * @return void    返回类型
         */
       public void addServeNode(String node)
       {
           lstServers.add(node);
           this.isInit=true;
       }
       
       /**
        * 
       * @Title: deleteServerNode
       * @Description:删除节点
       * @param @param node    参数
       * @return void    返回类型
        */
       public void deleteServerNode(String node)
       {
           lstServers.remove(node);
           this.isInit=true;
       }
        /**
         * 
        * @Title: getServer
        * @Description: 获取服务
        * @param
        * @param @return    参数
        * @return String    返回类型
         */
        public  String getServer(String node)
        {
            if(isInit)
            {
               init();
               isInit=false;
            }
            // 得到带路由的结点的Hash值
            int hash = getHash(node);
            // 得到大于该Hash值的所有Map
            SortedMap<Integer, String> subMap = 
                    virtualNodes.tailMap(hash);
            // 第一个Key就是顺时针过去离node最近的那个结点
            Integer i = subMap.firstKey();
            // 返回对应的虚拟节点名称，这里字符串稍微截取一下
            String virtualNode = subMap.get(i);
            return virtualNode.substring(0, virtualNode.indexOf("&&"));
        }
        
        /**
         * 
        * @Title: setServer
        * @Description: 设置服务节点
        * @param @param address    参数
        * @return void    返回类型
         */
        public  void setServer(List<String>address)
        {
            lstServers.addAll(address);
        }
    }


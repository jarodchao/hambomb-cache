/*
 * Copyright 2019 The  Project
 *
 * The   Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.hambomb.cache.cluster;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.hambomb.cache.cluster.node.CacheLoaderMaster;
import org.hambomb.cache.cluster.node.CacheLoaderSlave;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-28
 */
public class TestZookeeper {

    ZkClient zkClient;

    @Before
    public void setUp() throws Exception {

        zkClient = new ZkClient("localhost:2181", 5000, 5000, new SerializableSerializer());
    }

    @Test
    public void test_CreateMasterNode() {

        CacheLoaderMaster master = new CacheLoaderMaster();
        master.setIp("localhost");
        master.setHost("jarodchao");
        master.setJoinTime(LocalDateTime.now());
        master.setCreateTime(LocalDateTime.now());

        if (!zkClient.exists("/test")) {
            zkClient.create("/test", null, CreateMode.PERSISTENT);
        }

        if (!zkClient.exists("/test/master")) {
            String s = zkClient.create("/test/master", master, CreateMode.PERSISTENT);
            System.out.println(s);

            Stat stat = new Stat();
            CacheLoaderMaster master1 = zkClient.readData("/test/master", stat);
            System.out.println(master1.getCreateTime());
            zkClient.delete("/test/master");
        }
    }

    @Test
    public void test_CreateSlaveNode() {

        CacheLoaderSlave master = new CacheLoaderSlave();
        master.setIp("localhost");
        master.setHost("jarodchao");
        master.setJoinTime(LocalDateTime.now());
        master.setLoadUpdateTime(LocalDateTime.now());

        if (!zkClient.exists("/test")) {
            zkClient.create("/test", null, CreateMode.PERSISTENT);
        }

        if (!zkClient.exists("/test/slaves")) {
            zkClient.create("/test/slaves", master, CreateMode.EPHEMERAL_SEQUENTIAL);

            zkClient.create("/test/slaves", master, CreateMode.EPHEMERAL_SEQUENTIAL);
            String node = zkClient.create("/test/slaves", master, CreateMode.EPHEMERAL_SEQUENTIAL);

            Stat stat = new Stat();
            master.setHost("TomCat");
            zkClient.writeData(node, master);

            CacheLoaderSlave slave = zkClient.readData(node);
            System.out.println(slave.getHost());
        }
    }
}

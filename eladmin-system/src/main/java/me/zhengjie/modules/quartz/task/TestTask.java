/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.quartz.task;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.mnt.websocket.MsgType;
import me.zhengjie.modules.mnt.websocket.SocketMsg;
import me.zhengjie.modules.mnt.websocket.WebSocketServer;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 测试用
 * @author Zheng Jie
 * @date 2019-01-08
 */
@Slf4j
@Component
public class TestTask {

    private void sendMsg(String msg, MsgType msgType) {
        try {
            WebSocketServer.sendInfo(new SocketMsg(msg, msgType), "test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sleep(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(){

        log.info("run 执行成功");

        sendMsg("请假申请状态有变动，请查看1" , MsgType.success);
        sleep(5);
        sendMsg("请假申请状态有变动，请查看2" , MsgType.success);
        sleep(5);
        sendMsg("请假申请状态有变动，请查看3" , MsgType.success);
        sleep(5);
        sendMsg("请假申请状态有变动，请查看4" , MsgType.error);
        sleep(5);
        sendMsg("请假申请状态有变动，请查看5" , MsgType.error);
        sleep(5);
        sendMsg("请假申请状态有变动，请查看6" , MsgType.success);
        sleep(5);
        sendMsg("请假申请状态有变动，请查看7" , MsgType.success);
        sleep(5);
        sendMsg("请假申请状态有变动，请查看8" , MsgType.error);
        sleep(5);
        sendMsg("请假申请状态有变动，请查看9" , MsgType.error);
        sleep(5);
    }

//    public void run1(String str){
//        log.info("run1 执行成功，参数为： {}" + str);
//    }
//
//    public void run2(){
//        log.info("run2 执行成功");
//    }
}

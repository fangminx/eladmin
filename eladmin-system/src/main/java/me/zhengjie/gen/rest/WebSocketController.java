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
package me.zhengjie.gen.rest;

import com.alipay.api.java_websocket.WebSocket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.gen.domain.HolidayRecord;
import me.zhengjie.gen.service.HolidayRecordService;
import me.zhengjie.gen.service.dto.HolidayRecordQueryCriteria;
import me.zhengjie.modules.mnt.websocket.MsgType;
import me.zhengjie.modules.mnt.websocket.SocketMsg;
import me.zhengjie.modules.mnt.websocket.WebSocketServer;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @website https://el-admin.vip
* @author fangmin
* @date 2020-06-09
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "测试websocket")
@RequestMapping("/api/websocket")
public class WebSocketController {

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


    @Log("正在测试websocket")
    @ApiOperation("测试websocket")
    @GetMapping(value = "/send")
    public void download() throws IOException {

        while (true){
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
    }
}
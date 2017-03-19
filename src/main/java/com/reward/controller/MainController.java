package com.reward.controller;

import com.reward.resources.MusicResource;
import com.reward.util.CheckUtil;
import com.reward.util.MessageUtil;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by gjason on 2017/1/27.
 */
@RestController
public class MainController {
    Logger logger = LoggerFactory.getLogger(MainController.class);


    @RequestMapping(value = {"/", "index"}, method = RequestMethod.GET)
    @ResponseBody
    public String index(@RequestParam("signature") String signature,
                        @RequestParam("timestamp") String timestamp,
                        @RequestParam("echostr") String echostr,
                        @RequestParam("nonce") String nonce,
                        HttpServletResponse httpServletResponse) throws IOException {
//        PrintWriter out = httpServletResponse.getWriter();
        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        return null;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public String message(HttpServletRequest request,
                          HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        try {
            Map<String, String> map = MessageUtil.xmlToMap(request);
            String fromUserName = map.get("FromUserName");
            String toUserName = map.get("ToUserName");
            String createTime = map.get("CreateTime");
            String msgType = map.get("MsgType");
            String content = map.get("Content");
            String msgId = map.get("MsgId");
            String message = null;
            if (MessageUtil.MESSAGE_TEXT.equals(msgType)) {
                switch (content) {
                    case "1":
                        message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.firstText());
                        break;
                    case "2":
                        message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.secondText());
                        break;
                    default:
                        message = MessageUtil.initText(toUserName, fromUserName, MusicResource.getInstance().getMuiscPath(content));

                }
//                TextMessage text = new TextMessage();
//                text.setFromUserName(toUserName);
//                text.setToUserName(fromUserName);
//                text.setMsgType("text");
//                text.setCreateTime(new Date().getTime());
//                text.setContent("您发送的消息是：" + content);
//                message = MessageUtil.textMessageToXml(text);
            } else if (MessageUtil.MESSAGE_EVNET.equals(msgType)) {
                String eventType = map.get("Event");
                if (MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)) {
                    message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
                }
            }
            logger.info(message);
            return message;
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

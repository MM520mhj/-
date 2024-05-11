package org.example.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileOutputStream;

@RestController
public class MessagesController {

    @Resource
    private OpenAiChatClient openAiChatClient;

    @Resource
    private OpenAiImageClient openedAiImageClient;

    @Resource
    private OpenAiAudioTranscriptionClient openAiAudioTranscriptionClient;

    @Resource
    private OpenAiAudioSpeechClient openAiAudioSpeechClient;

    @PostMapping("/chat")
    public String getMessage(@RequestBody String message) {
        System.out.println(message);
        return openAiChatClient.call(message);
    }

    @RequestMapping("/ai/chat1")
    public Object getMessage1(@RequestParam(value = "message") String message) {
        System.out.println(message);
        Flux<ChatResponse> flux = openAiChatClient.stream(new Prompt(message, OpenAiChatOptions.builder().withTemperature(0.5F).build()));
        flux.toStream().forEach(chatResponse -> System.out.println(chatResponse.getResult().getOutput().getContent()));
        return flux.collectList();
    }

    @RequestMapping("/ai/chat2")
    public Object getMessage2(@RequestParam(value = "message") String message) {
        System.out.println(message);
        ImageResponse call = openedAiImageClient.call(new ImagePrompt(message));
        String url = call.getResult().getOutput().getUrl();
        return url;
    }

    @RequestMapping("/ai/chat3")
    public Object getMessage3() {
        ClassPathResource classPathResource = new ClassPathResource("ly.mp3");
        String call = openAiAudioTranscriptionClient.call(classPathResource);
        return call;
    }

    @RequestMapping("/ai/chat4")
    public Object getMessage4() {
        byte[] call = openAiAudioSpeechClient.call("蒋阳阳大聪明！");
        try {
            saveFile("ly.mp3",call);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字节流转换成文件
     * @param filename
     * @param data
     * @throws Exception
     */
    public static void saveFile(String filename,byte [] data)throws Exception{
        if(data != null){
            String filepath ="D:\\" + filename;
            File file  = new File(filepath);
            if(file.exists()){
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        }
    }

}

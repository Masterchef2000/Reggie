package com.cello.reggie.controller;

import com.cello.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.storagePath}")
    private String storagePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        log.info(file.toString());

        //重命名文件
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象,目录不存在则创建
        File dir = new File(storagePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        //上传图片
        try {
            file.transferTo(new File(storagePath + filename));
            log.info("文件存储路径为"+ storagePath + filename);
        }catch (IOException e){
            e.printStackTrace();
        }
        //服务器将文件名返回给前端，便于后续展示在浏览器上
        return R.success(filename);
    }

    /**
     * 1.通过输入流读取文件内容
     * 2.通过输出流将文件写回浏览器，在浏览器展示图片
     * 3.关闭输入输出流，释放资源
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try{
            //输入流
            FileInputStream fileInputStream = new FileInputStream(new File(storagePath + name));
            //输出流
            ServletOutputStream OutputStream = response.getOutputStream();
            //代表图片文件
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != 0){
                //向response缓冲区中写入bytes，再由tomcat服务器将字节内容组成Http相应返回服务器
                OutputStream.write(bytes,0,len);
                OutputStream.flush();
            }

            //关闭流
            fileInputStream.close();
            OutputStream.close();

        }catch (IOException e){
            e.printStackTrace();
        }


    }


}

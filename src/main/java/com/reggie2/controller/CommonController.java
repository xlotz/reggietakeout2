package com.reggie2.controller;

import com.reggie2.common.Result;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 上传下载，公共控制器
 * @author
 * @date 2023/8/9
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    // 定义上传路径
    @Value("${basePath.uploadPath}")
    private String uploadPath;

    /**
     * 上传文件到指定目录，并以UUID重命名文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        // file 为临时文件，需要转存到其他目录
        log.info("upload file: {}", file.toString());

        // 获取原始文件名
        String filename = file.getOriginalFilename();
        String subStr = ".jpg";

        if (filename != null){
            subStr = filename.substring(filename.lastIndexOf("."));
        }
        // 使用UUID 重新生成文件名，避免重复文件覆盖
        String newFilename = UUID.randomUUID().toString()+ subStr;

        File dir = new File(uploadPath + newFilename);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(uploadPath + newFilename));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Result.success(newFilename);

    }

    /**
     * 下载文件
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        response.setContentType("image/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];

        try {
            // 输入流， 读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(uploadPath + name));
            // 输出流，写回浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();

            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

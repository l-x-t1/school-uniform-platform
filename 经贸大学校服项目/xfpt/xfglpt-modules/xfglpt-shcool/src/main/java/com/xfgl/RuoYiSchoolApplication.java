package com.xfgl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import com.xfgl.common.security.annotation.EnableCustomConfig;
import com.xfgl.common.security.annotation.EnableRyFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 系统模块
 *
 * @author ruoyi
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
@EnableAsync
public class RuoYiSchoolApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(RuoYiSchoolApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  学校模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " __  __     ________      ___   __       ______       _________  \n" +
                "/_/\\/_/\\   /_______/\\    /__/\\ /__/\\    /_____/\\     /________/\\ \n" +
                "\\ \\ \\ \\ \\  \\__.::._\\/    \\::\\_\\\\  \\ \\   \\::::_\\/_    \\__.::.__\\/ \n" +
                " \\:\\_\\ \\ \\    \\::\\ \\      \\:. `-\\  \\ \\   \\:\\/___/\\      \\::\\ \\   \n" +
                "  \\::::_\\/    _\\::\\ \\__    \\:. _    \\ \\   \\::___\\/_      \\::\\ \\  \n" +
                "    \\::\\ \\   /__\\::\\__/\\    \\. \\`-\\  \\ \\   \\:\\____/\\      \\::\\ \\ \n" +
                "     \\__\\/   \\________\\/     \\__\\/ \\__\\/    \\_____\\/       \\__\\/ ");
    }
}

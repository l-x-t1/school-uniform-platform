package com.xfgl.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.xfgl.common.security.annotation.EnableCustomConfig;
import com.xfgl.common.security.annotation.EnableRyFeignClients;

/**
 * 定时任务
 *
 * @author ruoyi
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class RuoYiJobApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(RuoYiJobApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  定时任务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " __  __     ________      ___   __       ______       _________  \n" +
                "/_/\\/_/\\   /_______/\\    /__/\\ /__/\\    /_____/\\     /________/\\ \n" +
                "\\ \\ \\ \\ \\  \\__.::._\\/    \\::\\_\\\\  \\ \\   \\::::_\\/_    \\__.::.__\\/ \n" +
                " \\:\\_\\ \\ \\    \\::\\ \\      \\:. `-\\  \\ \\   \\:\\/___/\\      \\::\\ \\   \n" +
                "  \\::::_\\/    _\\::\\ \\__    \\:. _    \\ \\   \\::___\\/_      \\::\\ \\  \n" +
                "    \\::\\ \\   /__\\::\\__/\\    \\. \\`-\\  \\ \\   \\:\\____/\\      \\::\\ \\ \n" +
                "     \\__\\/   \\________\\/     \\__\\/ \\__\\/    \\_____\\/       \\__\\/ ");
    }
}

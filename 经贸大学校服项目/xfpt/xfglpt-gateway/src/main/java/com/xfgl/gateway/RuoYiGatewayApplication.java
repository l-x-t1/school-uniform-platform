package com.xfgl.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 网关启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class RuoYiGatewayApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(RuoYiGatewayApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  网关启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " __  __     ________      ___   __       ______       _________  \n" +
                "/_/\\/_/\\   /_______/\\    /__/\\ /__/\\    /_____/\\     /________/\\ \n" +
                "\\ \\ \\ \\ \\  \\__.::._\\/    \\::\\_\\\\  \\ \\   \\::::_\\/_    \\__.::.__\\/ \n" +
                " \\:\\_\\ \\ \\    \\::\\ \\      \\:. `-\\  \\ \\   \\:\\/___/\\      \\::\\ \\   \n" +
                "  \\::::_\\/    _\\::\\ \\__    \\:. _    \\ \\   \\::___\\/_      \\::\\ \\  \n" +
                "    \\::\\ \\   /__\\::\\__/\\    \\. \\`-\\  \\ \\   \\:\\____/\\      \\::\\ \\ \n" +
                "     \\__\\/   \\________\\/     \\__\\/ \\__\\/    \\_____\\/       \\__\\/ ");
    }
}

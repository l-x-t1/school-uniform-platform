var ofdSealNetTestSuit = new TestSuit("OFD签章(网络版)");

ofdSealNetTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        Trace.Warning("注意：OFD签章网络版，暂不支持联合签章和客户端签章，是由于网络版签章服务器签发的国密格式印章带有扩展项，而ofd签章要求不携带扩展项，所以网络版印章不能用于OFD签章。")
        test_createContext(testResult,context_net)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "创建文件签章环境",
    asyn: true,
    fun: function(testResult){
        test_createFileSealContext(testResult,context_net)
    }
});

/*
 由于签章服务器发放的国密章，有扩展项，而ofd签章不支持扩展项，所以这部分测试会出现问题，先去掉
ofdSealNetTestSuit.AddTest({
    name: "OFD联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_net,"&sealType=0&type=0&page=1&pos=100,100")
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_net,"&sealType=0&type=1&page=1&keyword=OFD")
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_net,"&sealType=0&type=2&offset=100")
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_net,"&sealType=0&type=3&offset=100")
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD客户端签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_net,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,false)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD客户端签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_net,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "OFD",
                offset: "0,0"
            }
        },0,0,false)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD客户端签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_net,{
            type: 2,
            riding: {
                side: "right",
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD客户端签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_net,{
            type: 3,
            cuttingRide: {
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD客户端签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_net,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,true)
    }
});

 */

ofdSealNetTestSuit.AddTest({
    name: "OFD反向联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_net,{
            location: {
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },0,0,false)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD反向联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_net,{
            multiShow: true,
            location: {
                type: 1,
                keyword: {
                    page: "1",
                    keyword: "OFD",
                    offset: "0,0"
                }
            }
        },0,0,false)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD反向联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_net,{
            location: {
                type: 2,
                riding: {
                    side: "right",
                    offset: "100",
                }
            }
        },0,0,false)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD反向联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_net,{
            location: {
                type: 3,
                cuttingRide: {
                    offset: "100",
                }
            }
        },0,0,false)
    }
});

ofdSealNetTestSuit.AddTest({
    name: "OFD反向联合签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_net,{
            location: {
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },0,0,true)
    }
});


allTestes.AddTestSuit(ofdSealNetTestSuit);
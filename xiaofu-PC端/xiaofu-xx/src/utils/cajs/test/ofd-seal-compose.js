var ofdSealComposeTestSuit = new TestSuit("OFD签章(混合环境)");

ofdSealComposeTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        Trace.Warning("注意：OFD签章混合环境，不支持自定义格式印章，不支持网络版国密格式印章，不支持签批。")
        test_createContext(testResult,context_compose)
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "创建文件签章环境",
    asyn: true,
    fun: function(testResult){
        test_createFileSealContext(testResult,context_compose)
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_compose,"&sealType=0&type=0&page=1&pos=100,100")
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_compose,"&sealType=0&type=1&page=1&keyword=OFD")
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_compose,"&sealType=0&type=2&offset=100")
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_compose,"&sealType=0&type=3&offset=100")
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD客户端签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_compose,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,false)
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD客户端签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_compose,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "OFD",
                offset: "0,0"
            }
        },0,0,false)
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD客户端签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_compose,{
            type: 2,
            riding: {
                side: "right",
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD客户端签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_compose,{
            type: 3,
            cuttingRide: {
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD客户端签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_compose,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,true)
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD反向联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_compose,{
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

ofdSealComposeTestSuit.AddTest({
    name: "OFD反向联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_compose,{
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

ofdSealComposeTestSuit.AddTest({
    name: "OFD反向联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_compose,{
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

ofdSealComposeTestSuit.AddTest({
    name: "OFD反向联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_compose,{
            location: {
                type: 3,
                cuttingRide: {
                    offset: "100",
                }
            }
        },0,0,false)
    }
});

ofdSealComposeTestSuit.AddTest({
    name: "OFD反向联合签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_compose,{
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


allTestes.AddTestSuit(ofdSealComposeTestSuit);
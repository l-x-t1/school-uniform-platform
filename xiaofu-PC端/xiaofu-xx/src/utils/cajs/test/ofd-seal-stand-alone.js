var ofdSealStandAloneTestSuit = new TestSuit("OFD签章(单机版)");

ofdSealStandAloneTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        Trace.Warning("注意：OFD签章单机版，不支持自定义格式印章，不支持签批功能")
        test_createContext(testResult,context_stand_alone)
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "创建文件签章环境",
    asyn: true,
    fun: function(testResult){
        test_createFileSealContext(testResult,context_stand_alone)
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_stand_alone,"&sealType=0&type=0&page=1&pos=100,100")
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_stand_alone,"&sealType=0&type=1&page=1&keyword=OFD")
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_stand_alone,"&sealType=0&type=2&offset=100")
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_stand_alone,"&sealType=0&type=3&offset=100")
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD客户端签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_stand_alone,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,false)
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD客户端签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_stand_alone,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "OFD",
                offset: "0,0"
            }
        },0,0,false)
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD客户端签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_stand_alone,{
            type: 2,
            riding: {
                side: "right",
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD客户端签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_stand_alone,{
            type: 3,
            cuttingRide: {
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD客户端签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_stand_alone,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,true)
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD反向联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_stand_alone,{
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

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD反向联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_stand_alone,{
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

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD反向联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_stand_alone,{
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

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD反向联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_stand_alone,{
            location: {
                type: 3,
                cuttingRide: {
                    offset: "100",
                }
            }
        },0,0,false)
    }
});

ofdSealStandAloneTestSuit.AddTest({
    name: "OFD反向联合签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_stand_alone,{
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


allTestes.AddTestSuit(ofdSealStandAloneTestSuit);
var ofdSealCloudTestSuit = new TestSuit("OFD签章(密码云)");

ofdSealCloudTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        Trace.Warning("注意：OFD签章密码云环境，不支持自定义格式印章")
        test_createContext(testResult,context_cloud)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "创建文件签章环境",
    asyn: true,
    fun: function(testResult){
        test_createFileSealContext(testResult,context_cloud)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_cloud,"&sealType=0&type=0&page=1&pos=100,100")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_cloud,"&sealType=0&type=1&page=1&keyword=OFD")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_cloud,"&sealType=0&type=2&offset=100")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHash(testResult,context_cloud,"&sealType=0&type=3&offset=100")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合文字签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashQP(testResult,context_cloud,"&sealType=1&type=0&page=1&pos=100,100")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合文字签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashQP(testResult,context_cloud,"&sealType=1&type=1&page=1&keyword=OFD")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合文字签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashQP(testResult,context_cloud,"&sealType=1&type=2&offset=100")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合文字签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashQP(testResult,context_cloud,"&sealType=1&type=3&offset=100")
    }
});


ofdSealCloudTestSuit.AddTest({
    name: "OFD联合图片签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashQP(testResult,context_cloud,"&sealType=2&type=0&page=1&pos=100,100")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合图片签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashQP(testResult,context_cloud,"&sealType=2&type=1&page=1&keyword=OFD")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合图片字签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashQP(testResult,context_cloud,"&sealType=2&type=2&offset=100")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD联合图片签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashQP(testResult,context_cloud,"&sealType=2&type=3&offset=100")
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "OFD",
                offset: "0,0"
            }
        },0,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 2,
            riding: {
                side: "right",
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 3,
            cuttingRide: {
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端文字签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },1,1,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端文字签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "OFD",
                offset: "0,0"
            }
        },1,1,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端文字签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 2,
            riding: {
                side: "right",
                offset: "100",
            }
        },1,1,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端文字签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 3,
            cuttingRide: {
                offset: "100",
            }
        },1,1,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端图片签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },1,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端图片签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "OFD",
                offset: "0,0"
            }
        },1,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端图片签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 2,
            riding: {
                side: "right",
                offset: "100",
            }
        },1,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端图片签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 3,
            cuttingRide: {
                offset: "100",
            }
        },1,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD客户端签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_cloud,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,true)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
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

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
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

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
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

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            location: {
                type: 3,
                cuttingRide: {
                    offset: "100",
                }
            }
        },0,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合文字签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            location: {
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },1,1,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合文字签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            multiShow: true,
            location: {
                type: 1,
                keyword: {
                    page: "1",
                    keyword: "OFD",
                    offset: "0,0"
                }
            }
        },1,1,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合文字签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            location: {
                type: 2,
                riding: {
                    side: "right",
                    offset: "100",
                }
            }
        },1,1,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合文字签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            location: {
                type: 3,
                cuttingRide: {
                    offset: "100",
                }
            }
        },1,1,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合图片签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            location: {
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },1,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合图片签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            multiShow: true,
            location: {
                type: 1,
                keyword: {
                    page: "1",
                    keyword: "OFD",
                    offset: "0,0"
                }
            }
        },1,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合图片签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            location: {
                type: 2,
                riding: {
                    side: "right",
                    offset: "100",
                }
            }
        },1,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合图片签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
            location: {
                type: 3,
                cuttingRide: {
                    offset: "100",
                }
            }
        },1,0,false)
    }
});

ofdSealCloudTestSuit.AddTest({
    name: "OFD反向联合签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashFx(testResult,context_cloud,{
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


allTestes.AddTestSuit(ofdSealCloudTestSuit);
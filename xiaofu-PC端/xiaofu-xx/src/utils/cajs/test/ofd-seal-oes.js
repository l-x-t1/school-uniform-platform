var ofdSealOesTestSuit = new TestSuit("OFD签章(OES插件)");

var context_oes="default-oes"

ofdSealOesTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        Trace.Warning("注意：OFD签章OES签章插件环境，不支持自定义格式印章，仅支持联合签章和客户端签章，不支持签批。")
        test_createContext(testResult,context_oes)
    }
});

ofdSealOesTestSuit.AddTest({
    name: "创建文件签章环境",
    asyn: true,
    fun: function(testResult){
        test_createFileSealContext(testResult,context_oes)
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashOes(testResult,context_oes,"&sealType=0&type=0&page=1&pos=100,100")
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashOes(testResult,context_oes,"&sealType=0&type=1&page=1&keyword=OFD")
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashOes(testResult,context_oes,"&sealType=0&type=2&offset=100")
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignSealHashOes(testResult,context_oes,"&sealType=0&type=3&offset=100")
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD客户端签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_oes,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,false)
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD客户端签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_oes,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "OFD",
                offset: "0,0"
            }
        },0,0,false)
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD客户端签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_oes,{
            type: 2,
            riding: {
                side: "right",
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD客户端签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_oes,{
            type: 3,
            cuttingRide: {
                offset: "100",
            }
        },0,0,false)
    }
});

ofdSealOesTestSuit.AddTest({
    name: "OFD客户端签章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_ofdSignClientFile(testResult,context_oes,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,0,true)
    }
});

allTestes.AddTestSuit(ofdSealOesTestSuit);
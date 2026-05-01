var sealNetTestSuit = new TestSuit("数据签章(网络版)");

var context_net="default-net"

sealNetTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        test_createContext(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "列举印章",
    asyn: true,
    fun: function(testResult){
        test_listSeal(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "获取印章数据",
    asyn: true,
    fun: function(testResult){
        test_getSealB64(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "获取印章信息",
    asyn: true,
    fun: function(testResult){
        test_getSealInfo(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "整体签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSealSignature(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "整体签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealSignatureBatch(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "分散签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSeal(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "分散签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealBatch(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "验证证书（默认）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "验证证书（local）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_net,"local")
    }
});

sealNetTestSuit.AddTest({
    name: "验证证书（cloud）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_net,"cloud")
    }
});

sealNetTestSuit.AddTest({
    name: "验证证书（svs）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_net,"svs")
    }
});


sealNetTestSuit.AddTest({
    name: "验证印章（默认）",
    asyn: true,
    fun: function(testResult){
        test_verifySeal(testResult,context_net)
    }
});

sealNetTestSuit.AddTest({
    name: "验证印章（local）",
    asyn: true,
    fun: function(testResult){
        test_verifySeal(testResult,context_net,"local","local")
    }
});


allTestes.AddTestSuit(sealNetTestSuit);

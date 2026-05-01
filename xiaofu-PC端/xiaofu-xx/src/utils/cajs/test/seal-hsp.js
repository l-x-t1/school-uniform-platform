var sealHspTestSuit = new TestSuit("数据签章(河北CA签章插件环境)<br>");

var context_hsp="default-hsp"

sealHspTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        test_createContext(testResult,context_hsp)
    }
});

sealHspTestSuit.AddTest({
    name: "列举印章",
    asyn: true,
    fun: function(testResult){
        test_listSeal(testResult,context_hsp)
    }
});

sealHspTestSuit.AddTest({
    name: "获取印章数据",
    asyn: true,
    fun: function(testResult){
        test_getSealB64(testResult,context_hsp)
    }
});

sealHspTestSuit.AddTest({
    name: "获取印章信息",
    asyn: true,
    fun: function(testResult){
        test_getSealInfo(testResult,context_hsp,true)
    }
});

sealHspTestSuit.AddTest({
    name: "整体签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSealSignature(testResult,context_hsp,true)
    }
});

sealHspTestSuit.AddTest({
    name: "整体签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealSignatureBatch(testResult,context_hsp)
    }
});

sealHspTestSuit.AddTest({
    name: "分散签章和验证",
    asyn: true,
    fun: function(testResult){
        test_signSeal(testResult,context_hsp,true)
    }
});

sealHspTestSuit.AddTest({
    name: "分散签章和验证-批量",
    asyn: true,
    fun: function(testResult){
        test_signSealBatch(testResult,context_hsp)
    }
});

sealHspTestSuit.AddTest({
    name: "验证证书（默认）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_hsp)
    }
});

sealHspTestSuit.AddTest({
    name: "验证证书（svs）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_hsp,"svs")
    }
});

sealHspTestSuit.AddTest({
    name: "验证证书（sealService）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_hsp,"sealService")
    }
});

sealHspTestSuit.AddTest({
    name: "验证证书（cloud）",
    asyn: true,
    fun: function(testResult){
        test_verifyCert(testResult,context_hsp,"cloud")
    }
});

sealHspTestSuit.AddTest({
    name: "验证印章（默认）",
    asyn: true,
    fun: function(testResult){
        test_verifySeal(testResult,context_hsp)
    }
});


allTestes.AddTestSuit(sealHspTestSuit);

// 存放主要交互逻辑js代码
// javascript 模块化
var seckill = {
    // 封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        }
    }
    , validatePhone: function (phone) {
        return phone && phone.length == 11 && !isNaN(phone);
    }
    // 时间判断
    , countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        console.log(seckillId, nowTime, startTime, endTime);
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束！');
        } else if (nowTime < startTime) {
            var killTime = new Date(startTime+1000);
            seckillBox.countdown(killTime, function(event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            });
        } else {
            // 秒杀开始
            console.log('秒杀开始') // TODO
        }
    }
    , detail: {
        // 详情页初始化
        init: function (params) {
            // 用户手机验证和登录，计时交互
            // 规划交互流程
            // 在cookie中查找手机号
            var killPhone = $.cookie('killPhone');

            // 验证手机号
            if (!seckill.validatePhone(killPhone)) {
                // 绑定phone 控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,
                    backdrop: 'static', // 禁止位置关闭
                    keyboard: false
                });

                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log('inputPhone=' + inputPhone); // TODO
                    if (seckill.validatePhone(inputPhone)) {
                        // 刷新页面
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>>').show(300);

                    }

                });
            }

            // 已经登录
            // 计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];

                    // 时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                }
                else {
                    console.log('result:' + result);
                }
            });
        }
    }
};
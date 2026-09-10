<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Document</title>
</head>
<body>
<div style="
        color: #1f2329;
        font-family: LarkEmojiFont, LarkChineseQuote, -apple-system, system-ui,
          'Helvetica Neue', Tahoma, 'PingFang SC', 'Microsoft Yahei', Arial,
          'Hiragino Sans GB', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji',
          'Segoe UI Symbol', 'Noto Color Emoji';
        font-size: 16px;
        font-weight: 400;
        background-color: rgb(248, 248, 248);
        width: 100%;
        padding-top: 10px;
        line-height: 25px;">
    <div style=" padding: 20px 20px 40px 20px; background-color: #fff; text-align: left;">
        <div style="font-size: 18px; font-weight: bold; color: #4081CC; margin-bottom: 10px;">
            Account Balance Snapshot-${nowTime}
        </div>
        <table style="width: 70%; margin: 5px 5px; padding: 5px 0px; background-color: #FFFFFF;
            border-collapse: collapse; font-size: 14px; border: 1px solid #D9D9D9; text-align: center;
            white-space: nowrap;">
            <tr style="color: #4081CC; background-color: #DDEBF7;">
                <th style="min-width: 80px; border: 1px solid #D9D9D9; padding: 4px 0px;"> Country</th>
                <th style="min-width: 120px; border: 1px solid #D9D9D9;">Merchant</th>
                <th style="min-width: 120px; border: 1px solid #D9D9D9;">TransactionType</th>
                <th style="min-width: 120px; border: 1px solid #D9D9D9;">Account Balance</th>
                <th style="min-width: 80px; border: 1px solid #D9D9D9;">Currency</th>
                <th style="min-width: 80px; border: 1px solid #D9D9D9;">Timezone</th>
            </tr>
            <#list dataList as data>
                <tr>
                    <th style="border: 1px solid #D9D9D9; padding: 4px 4px;">${data.countryCode}</th>
                    <td style="border: 1px solid #D9D9D9;">${data.merchantCode}</td>
                    <td style="border: 1px solid #D9D9D9;">${data.transactionTypeCode}</td>
                    <td style="border: 1px solid #D9D9D9;">${data.accountBalance}</td>
                    <td style="border: 1px solid #D9D9D9;">${data.currency}</td>
                    <td style="border: 1px solid #D9D9D9;">${data.timezone}</td>
                </tr>
            </#list>
        </table>
        <div>Best Regards</div>
    </div>
    <div style="padding: 20px; margin-top: 5px; text-align: center">
        <div style="border-top: 3px solid gray; margin-bottom: 40px"></div>
        <div style="
            color: rgb(51, 51, 51);
            font-weight: normal;
            font-size: 14px;
            line-height: 32px;
          ">
            <div>This email address is not available to receive replies.</div>
            <div>
                Feel free to contact <a style="color: rgb(51, 112, 255)" href="payoutfunding@liquido.com">payoutfunding@liquido.com</a>
                if you have any questions.
            </div>
        </div>
    </div>
</div>
</body>
</html>

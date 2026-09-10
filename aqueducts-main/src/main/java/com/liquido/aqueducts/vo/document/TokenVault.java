package com.liquido.aqueducts.vo.document;

import com.liquido.aqueducts.util.CommonUtil;
import com.liquido.aqueducts.util.Md5Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenVault {
    private String token;
    private String merchant_name;
    private String type;
    private String vendor;
    private String vendor_info;
    private String id;
    private String token_info;
    private String create_time;
    private String update_time;

    public String getMaskToken() {
        if (!this.token.matches("card_.*")) {
            return CommonUtil.maskString(this.token);
        }
        String[] list = this.token.split("_");
        return list[0] + "_" + CommonUtil.maskString(list[1]);
    }

    public String getMd5Token() {
        return Md5Util.getMd5(this.token);
    }

}

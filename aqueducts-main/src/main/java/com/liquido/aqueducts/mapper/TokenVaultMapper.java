package com.liquido.aqueducts.mapper;

import com.liquido.aqueducts.config.BaseMapperConfig;
import com.liquido.aqueducts.util.DateUtil;
import com.liquido.aqueducts.util.JsonUtil;
import com.liquido.aqueducts.vo.document.eventlog.TokenVaultEventLog;
import com.liquido.aqueducts.vo.response.eventlog.TokenVaultItem;
import org.bson.types.ObjectId;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public abstract class TokenVaultMapper {

    public abstract List<TokenVaultItem> eventLogToTokenVault(
            final List<TokenVaultEventLog> eventLogs);

    @Mapping(target = "merchantName", source = "after.merchant_name")
    @Mapping(target = "type", source = "after.type")
    @Mapping(target = "vendor", source = "after.vendor")
    @Mapping(target = "id", source = "after.id")
    public abstract TokenVaultItem toEventLog(final TokenVaultEventLog eventLog);

    @AfterMapping
    void afterMapping(final TokenVaultEventLog eventLog, @MappingTarget TokenVaultItem tokenVault) {
        tokenVault.setToken(eventLog.getAfter().getMaskToken());
        tokenVault.setTokenMd5(eventLog.getAfter().getMd5Token());

        tokenVault.setTokenInfo(JsonUtil.parseJson(eventLog.getAfter().getToken_info()));
        tokenVault.setVendorInfo(JsonUtil.parseJson(eventLog.getAfter().getVendor_info()));

        tokenVault.setCreateTime(
                DateUtil.UTCDateToTimestamp(eventLog.getAfter().getCreate_time()) / 1000);
        tokenVault.setUpdateTime(
                DateUtil.UTCDateToTimestamp(eventLog.getAfter().getUpdate_time()) / 1000);

        final ObjectId id = (ObjectId) eventLog.get_id();
        tokenVault.setEventTime(id.getTimestamp());
    }


}

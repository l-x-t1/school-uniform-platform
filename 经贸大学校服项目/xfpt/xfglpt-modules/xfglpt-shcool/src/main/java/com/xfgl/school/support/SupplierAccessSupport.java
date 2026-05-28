package com.xfgl.school.support;

import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.api.domain.SysUser;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupplierAccessSupport {
    @Autowired
    private ISupplierMessageService supplierMessageService;

    public boolean isSupplierUser(SysUser sysUser) {
        if (sysUser == null) {
            return false;
        }
        List<SysRole> roles = sysUser.getRoles();
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        for (SysRole role : roles) {
            if (role == null) {
                continue;
            }
            if (Long.valueOf(115L).equals(role.getRoleId())) {
                return true;
            }
            if (StringUtils.equalsAnyIgnoreCase(role.getRoleKey(), "gys", RoleType.CSGY.getCode())) {
                return true;
            }
        }
        return false;
    }

    public Long resolveSupplierId(SysUser sysUser) {
        if (!isSupplierUser(sysUser)) {
            return null;
        }
        SupplierMessage directSupplier = findSupplierById(sysUser.getUserId());
        if (directSupplier != null) {
            return directSupplier.getId();
        }

        Set<String> candidateNames = new LinkedHashSet<>();
        addCandidate(candidateNames, sysUser.getUserName());
        addCandidate(candidateNames, sysUser.getNickName());
        for (String candidate : candidateNames) {
            SupplierMessage matchedSupplier = findSupplierByName(candidate);
            if (matchedSupplier != null) {
                return matchedSupplier.getId();
            }
        }

        SupplierMessage matchedByPhone = findSupplierByPhone(sysUser.getPhonenumber());
        return matchedByPhone == null ? null : matchedByPhone.getId();
    }

    private void addCandidate(Set<String> candidateNames, String value) {
        if (StringUtils.isNotBlank(value)) {
            candidateNames.add(value.trim());
        }
    }

    private SupplierMessage findSupplierById(Long supplierId) {
        if (supplierId == null) {
            return null;
        }
        return supplierMessageService.getById(supplierId);
    }

    private SupplierMessage findSupplierByName(String supplierName) {
        if (StringUtils.isBlank(supplierName)) {
            return null;
        }
        return supplierMessageService.lambdaQuery()
                .eq(SupplierMessage::getSupplierName, supplierName.trim())
                .last("LIMIT 1")
                .one();
    }

    private SupplierMessage findSupplierByPhone(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return null;
        }
        return supplierMessageService.lambdaQuery()
                .eq(SupplierMessage::getSupplierPhone, phoneNumber.trim())
                .last("LIMIT 1")
                .one();
    }
}

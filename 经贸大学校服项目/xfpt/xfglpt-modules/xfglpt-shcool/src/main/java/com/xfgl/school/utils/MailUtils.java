package com.xfgl.school.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件发送工具类（SMTP 未配置时静默跳过）
 */
@Slf4j
@Component
public class MailUtils {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    /**
     * 发送入驻申请通过通知邮件
     */
    @Async
    public void sendApproveEmail(String to, String supplierName) {
        if (mailSender == null) {
            log.warn("邮件服务未配置(SMTP)，跳过发送通过通知, to={}", to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("【校服管理平台】供应商入驻申请已通过");
            helper.setText(buildApproveHtml(supplierName), true);
            mailSender.send(message);
            log.info("入驻通过邮件发送成功, to={}, supplierName={}", to, supplierName);
        } catch (MessagingException e) {
            log.error("入驻通过邮件发送失败, to={}, supplierName={}", to, supplierName, e);
        }
    }

    /**
     * 发送入驻申请驳回通知邮件
     */
    @Async
    public void sendRejectEmail(String to, String supplierName, String remark) {
        if (mailSender == null) {
            log.warn("邮件服务未配置(SMTP)，跳过发送驳回通知, to={}", to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("【校服管理平台】供应商入驻申请未通过");
            helper.setText(buildRejectHtml(supplierName, remark), true);
            mailSender.send(message);
            log.info("入驻驳回邮件发送成功, to={}, supplierName={}", to, supplierName);
        } catch (MessagingException e) {
            log.error("入驻驳回邮件发送失败, to={}, supplierName={}", to, supplierName, e);
        }
    }

    private String buildApproveHtml(String supplierName) {
        return "<html><body style='font-family:Microsoft YaHei,sans-serif;padding:20px;'>"
                + "<h2 style='color:#21a67a;'>入驻申请已通过</h2>"
                + "<p>尊敬的 <strong>" + escapeHtml(supplierName) + "</strong>：</p>"
                + "<p>您在石家庄市校服管理平台提交的供应商入驻申请已<strong style='color:#21a67a;'>审核通过</strong>。</p>"
                + "<p>您可以使用提交时设置的手机号和密码登录平台。</p>"
                + "<br/><p style='color:#999;font-size:12px;'>此邮件由系统自动发送，请勿回复。</p>"
                + "</body></html>";
    }

    private String buildRejectHtml(String supplierName, String remark) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Microsoft YaHei,sans-serif;padding:20px;'>")
                .append("<h2 style='color:#f56c6c;'>入驻申请未通过</h2>")
                .append("<p>尊敬的 <strong>").append(escapeHtml(supplierName)).append("</strong>：</p>")
                .append("<p>您在石家庄市校服管理平台提交的供应商入驻申请<strong style='color:#f56c6c;'>未通过审核</strong>。</p>");
        if (remark != null && !remark.trim().isEmpty()) {
            sb.append("<p>驳回原因：").append(escapeHtml(remark.trim())).append("</p>");
        }
        sb.append("<p>如有疑问，请联系教育局。</p>")
                .append("<br/><p style='color:#999;font-size:12px;'>此邮件由系统自动发送，请勿回复。</p>")
                .append("</body></html>");
        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
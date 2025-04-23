package com.example.ttcs_jobwebsite.dto.job;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobInput {
    @NotEmpty(message = "Tên công việc không được để trống")
    private String jobName;

    @NotEmpty(message = "Ngành nghề không được để trống")
    private String occupationName;

    @NotEmpty(message = "Kinh nghiệm không được để trống")
    private String experience;

    @NotNull(message = "Số lượng tuyển không được để trống")
    @Min(value = 1, message = "Số lượng tuyển phải lớn hơn 0")
    private Integer headCount;

    @NotNull(message = "Hạn nộp hồ sơ không được để trống")
    @Future(message = "Hạn nộp hồ sơ phải là ngày trong tương lai")
    private LocalDateTime expirationDate;

    @NotEmpty(message = "Tỉnh/thành phố không được để trống")
    private String province;

    @NotEmpty(message = "Loại công việc không được để trống")
    private String jobType;

    @NotEmpty(message = "Cấp bậc không được để trống")
    private String jobLevel;

    @NotEmpty(message = "Mức lương tối thiểu không được để trống")
    @Pattern(regexp = "^(?!0\\.00$)(?!0$)([1-9][0-9]*)(\\.\\d{1,2})?$", message = "Mức lương tối thiểu phải là số dương, không được âm hoặc chứa chữ")
    private String minSalary;

    @NotEmpty(message = "Mức lương tối đa không được để trống")
    @Pattern(regexp = "^(?!0\\.00$)(?!0$)([1-9][0-9]*)(\\.\\d{1,2})?$", message = "Mức lương tối đa phải là số dương, không được âm hoặc chứa chữ")
    private String maxSalary;

    @NotEmpty(message = "Trình độ học vấn không được để trống")
    private String educationLevel;

    @NotEmpty(message = "Mô tả công việc không được để trống")
    private String descriptions;

    @NotEmpty(message = "Yêu cầu công việc không được để trống")
    private String requiredJobList;

    @NotEmpty(message = "Quyền lợi nhân viên không được để trống")
    private String employeeBenefitList;

    @NotEmpty(message = "Địa chỉ làm việc không được để trống")
    private String address;
}

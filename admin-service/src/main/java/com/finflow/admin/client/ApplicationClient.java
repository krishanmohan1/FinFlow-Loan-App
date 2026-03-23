package com.finflow.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "APPLICATION-SERVICE")
public interface ApplicationClient {

    @PutMapping("/application/status/{id}")
    String updateStatus(@PathVariable Long id,
                        @RequestParam String status);

    @DeleteMapping("/application/{id}")
    String delete(@PathVariable Long id);
}
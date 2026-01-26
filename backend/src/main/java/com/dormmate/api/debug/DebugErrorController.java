package com.dormmate.api.debug;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/debug/errors")
public class DebugErrorController {

    @GetMapping
    public void raise(@RequestParam(name = "status", defaultValue = "500") int status) {
        HttpStatus resolved = HttpStatus.resolve(status);
        if (resolved == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 상태 코드입니다.");
        }
        throw new ResponseStatusException(resolved, "테스트용 오류입니다.");
    }
}

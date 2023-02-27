package no.ntnu.sondrmal.webcompiler.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@RestController
public class WebCompilerController {
    private String codeOutput = "";

    Logger logger = LoggerFactory.getLogger(WebCompilerController.class);


    // for testing
    public static void main(String[] args) throws IOException {
        WebCompilerController w = new WebCompilerController();
        w.runPythonCode();
        System.out.println(w.codeOutput);
    }

    @PostMapping("/code")
    public ResponseEntity runCode(@RequestParam("code") String code) throws IOException {
        updatePythonFile(code);
        runPythonCode();

        return new ResponseEntity(this.codeOutput, HttpStatus.OK);
    }

    // Updates code.py with content from parameter
        public void updatePythonFile(String code) throws FileNotFoundException {
            //TODO: implement

    }


    // Runs docker which executes the code.py file
    public void runPythonCode() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        // runs docker process with updated code.py file
        Process process = runtime.exec("docker run --mount type=bind,source=./code.py,target=/code.py compiler-docker");
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        while ((line = br.readLine()) != null) {
            codeOutput += (line + "\n");
        }
    }


}

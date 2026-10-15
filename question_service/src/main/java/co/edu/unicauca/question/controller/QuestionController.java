package co.edu.unicauca.question.controller;

import co.edu.unicauca.question.dto.SubmitCommandDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final RabbitTemplate rabbitTemplate;

    public QuestionController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/{questionId}/submit")
    public ResponseEntity<Map<String, String>> submitForReview(@PathVariable Long questionId) {
        String operationId = UUID.randomUUID().toString();
        SubmitCommandDTO command = new SubmitCommandDTO(questionId, operationId);
        // Publicación asíncrona a RabbitMQ
        rabbitTemplate.convertAndSend("saberpro.direct.exchange", "question.submit",
                command);
        // Respuesta inmediata sin esperar la ejecución del Lifecycle Service
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "message", "Solicitud de envío a revisión aceptada para procesamiento.",
                "operationId", operationId,
                "status", "PROCESSING"));
    }
}
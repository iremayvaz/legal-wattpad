package com.iremayvaz.moderation.service;

/*public class MLClientService {
    // REST ile çağırıyoruz (Python Process)
    // veya
    // FastAPI (Python)
}*/

import com.iremayvaz.moderation.model.dto.ModerationResult;

public interface MLClientService {
    ModerationResult analyzeText(String text);
}


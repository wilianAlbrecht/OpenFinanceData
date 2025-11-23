package com.financial.openfinancedata.session;

import com.financial.openfinancedata.model.ModelSession;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter @Setter
public class YahooSessionStore {

    private ModelSession currentState = new ModelSession();
}

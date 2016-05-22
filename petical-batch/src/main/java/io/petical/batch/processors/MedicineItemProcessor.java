package io.petical.batch.processors;

import org.majimena.petical.domain.Medicine;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import rx.Observable;

/**
 * Created by todoken on 2016/05/21.
 */
@Component
public class MedicineItemProcessor implements ItemProcessor<Observable<Medicine>, Observable<Medicine>> {
    @Override
    public Observable<Medicine> process(Observable<Medicine> medicine) throws Exception {
        // Conversion Processing
        return medicine;
    }
}

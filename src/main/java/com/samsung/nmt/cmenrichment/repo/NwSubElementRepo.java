package com.samsung.nmt.cmenrichment.repo;

import java.util.List;
import java.util.Map;

import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.NwSubElKey;
import com.samsung.nmt.cmenrichment.dto.NwSubElement;

public interface NwSubElementRepo {

    int[][] addNwElements(List<NwSubElement> nwSubElements);

    int[][] updateNwElementProperties(List<NwSubElement> nwSubElements);

    int[][] deleteNwSubElements(List<NwSubElement> nwSubElements);

    /*Map<NwSubElKey, NwSubElement> getBytypeIdAndElementId(String elementIds,
            String typeIds);*/

    List<Long> addHistoryAndGetIds(List<HistoryData<NwSubElement>> nwSubElementHistory);

    int[][] addHistory(List<NwSubElement> nwSubElements);

    int[][] addHistoryMatric(List<MatricHistory> matricHistories);

    Map<NwSubElKey, NwSubElement> getBytypeIdAndElementIdAndName(String elementIds,
            String typeIds, String identNames, String locationIds);
}

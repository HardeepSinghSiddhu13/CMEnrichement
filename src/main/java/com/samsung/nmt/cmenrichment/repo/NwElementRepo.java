package com.samsung.nmt.cmenrichment.repo;

import java.util.List;
import java.util.Map;

import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.NwElement;

public interface NwElementRepo extends MetadataRepo<String> {
    Integer addAndGetId(NwElement nwElement);

    Map<String, Integer> addAndGetIdsInBatch(List<NwElement> nwElements);

    int updateAllExceptNameUsingId(NwElement nwElement);

    int[][] updateInBatch(List<NwElement> nwElements);

    int updateProperties(NwElement nwElement);

    int[][] updatePropertiesInBatch(List<NwElement> nwElements);

    int updateAllMutuables(NwElement nwElement);

    NwElement getNwElement(Integer elementId);

    NwElement getNwElement(String neName);

    Map<String, NwElement> getAllByNames(String neNamesInClause);

    Integer getId(String neName);

    int[][] deleteNwElementsInBatch(List<NwElement> nwElements);

    Integer addHistoryAndGetId(NwElement nwElement);

    int[][] addHistoryInBatch(List<NwElement> nwElements);

    List<Long> addHistoryAndGetIdsInBatch(List<HistoryData<NwElement>> nwElementHistory);

    int[][] addHistoryMatric(int historyId, List<Integer> metadataIds);

    int[][] addHistoryMatricInBatch(List<MatricHistory> matricHistories);

    Integer getIdFromCache(String neName);
}

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SuppressWarnings("Duplicates") class BakeResults {
    private static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                                                           .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    private static TypeReference listIntTypeRef = new TypeReference<List<Long>>() {
    };
    static Executor executor = Executors.newFixedThreadPool(4);
    private static final long maxAttributeValue = 30;
    private static final long dicesFaces = 20;

    private static void compute1v1() throws IOException {
        // 1 burst each, iterate on attribute value to be able to use this baseline to calculate for more dices
        for (long blueAttribute = 1; blueAttribute <= maxAttributeValue; blueAttribute++) {
            for (long redAttribute = 1; redAttribute <= maxAttributeValue; redAttribute++) {
                Map<String, Map<String, Map<String, Long>>> bakedForCurrentBlueAndRedAttribute = new HashMap<>();
                for (long blueThrow = 1; blueThrow <= dicesFaces; blueThrow++) {
                    for (long redThrow = 1; redThrow <= dicesFaces; redThrow++) {
                        long realBlueResult = blueAttribute > dicesFaces ? blueThrow + blueAttribute - dicesFaces : blueThrow;
                        long realRedResult = redAttribute > dicesFaces ? redThrow + redAttribute - dicesFaces : redThrow;

                        long maxBlueSuccess = realBlueResult < blueAttribute && realBlueResult < dicesFaces ? realBlueResult : 0;
                        long maxRedSuccess = realRedResult < redAttribute && realRedResult < dicesFaces ? realRedResult : 0;
                        long nbBlueCrits = realBlueResult == blueAttribute || (blueAttribute >= dicesFaces && realBlueResult >= dicesFaces) ? 1 : 0;
                        long nbRedCrits = realRedResult == redAttribute || (redAttribute >= dicesFaces && realRedResult >= dicesFaces) ? 1 : 0;
                        long[] blueSuccess = nbRedCrits == 0 && maxRedSuccess < maxBlueSuccess ? new long[] { maxBlueSuccess } : new long[] {};
                        long[] redSuccess = nbBlueCrits == 0 && maxRedSuccess > maxBlueSuccess ? new long[] { maxRedSuccess } : new long[] {};
                        if (nbBlueCrits > 0 && nbRedCrits > 0) {
                            //keep crits info but on the same key for all to simplify a little
                            nbBlueCrits = 1;
                            nbRedCrits = 1;
                        }

                        String key3 = String.format("c%sh%svc%sh%s", nbBlueCrits, blueSuccess.length, nbRedCrits, redSuccess.length);
                        // blue success list
                        String key4 = mapper.writeValueAsString(blueSuccess) + ":" + maxBlueSuccess;
                        // red success list
                        String key5 = mapper.writeValueAsString(redSuccess) + ":" + maxRedSuccess;

                        bakedForCurrentBlueAndRedAttribute.putIfAbsent(key3, new HashMap<>());
                        Map<String, Map<String, Long>> bakedForCurrentBlueAndRedAttributeForResult = bakedForCurrentBlueAndRedAttribute.get(key3);

                        bakedForCurrentBlueAndRedAttributeForResult.putIfAbsent(key4, new HashMap<>());
                        Map<String, Long> bakedForCurrentBlueAndRedAttributeForResultWmaxblue = bakedForCurrentBlueAndRedAttributeForResult.get(
                                key4);

                        bakedForCurrentBlueAndRedAttributeForResultWmaxblue.putIfAbsent(key5, 0L);
                        bakedForCurrentBlueAndRedAttributeForResultWmaxblue.put(key5,
                                                                                bakedForCurrentBlueAndRedAttributeForResultWmaxblue.get(key5) + 1);
                    }
                }
                FileUtils.writeStringToFile(
                        new File("bakedresults/temp/1v1/1dicesat" + blueAttribute + "v1dicesat" + redAttribute + ".json"),
                        mapper.writeValueAsString(bakedForCurrentBlueAndRedAttribute));
            }
        }
    }

    private static void computeNvNfromN(long originBlueDices, long originRedDices, boolean addDiceToRed) {
        long blueDices = addDiceToRed ? originBlueDices : originBlueDices + 1;
        long redDices = addDiceToRed ? originRedDices + 1 : originRedDices;
        for (long blueAttribute = 1; blueAttribute <= maxAttributeValue; blueAttribute++) {
            for (long redAttribute = 1; redAttribute <= maxAttributeValue; redAttribute++) {
                File outputFile = new File("bakedresults/temp/" + blueDices + "v" + redDices + "/" +
                                                   blueDices + "dicesat" + blueAttribute + "v" + redDices + "dicesat" + redAttribute + ".json");
                if (outputFile.exists()) {
                    System.out.println("skipping " + outputFile.getName());
                    continue;
                }

                long finalBlueAttribute = blueAttribute;
                long finalRedAttribute = redAttribute;
                executor.execute(() -> computeWithAttributes(finalBlueAttribute, finalRedAttribute, originBlueDices, originRedDices, addDiceToRed,
                                                             outputFile));
            }
        }

    }

    private static void computeWithAttributes(long blueAttribute, long redAttribute, long originBlueDices, long originRedDices, boolean addDiceToRed,
                                              File outputFile) {
        try {
            System.out.println("resolving " + outputFile.getName());
            long start = System.currentTimeMillis();
            Map<String, Map<String, Map<String, Long>>> bakedForCurrentBlueAndRedAttribute =
                    mapper.readValue(
                            FileUtils.readFileToString(new File(
                                    "bakedresults/temp/" + originBlueDices + "v" + originRedDices + "/" +
                                            originBlueDices + "dicesat" + blueAttribute + "v" + originRedDices + "dicesat" + redAttribute
                                            + ".json")),
                            new TypeReference<HashMap<String, HashMap<String, HashMap<String, Long>>>>() {
                            });
            Map<String, Map<String, Map<String, Long>>> bakedResult = new HashMap<>();
            long currentStep = 0;
            long totalStep = bakedForCurrentBlueAndRedAttribute.size();
            for (Entry<String, Map<String, Map<String, Long>>> entry3 : bakedForCurrentBlueAndRedAttribute.entrySet()) {
                currentStep++;
                System.out.print('\r');
                System.out.print(StringUtils.leftPad(currentStep + "/" + totalStep, 20));
                System.out.print('\r');
                String[] split = StringUtils.split(StringUtils.removeAll(entry3.getKey(), "c"), "v");
                String[] blueSplit = StringUtils.split(split[0], "h");
                String[] redSplit = StringUtils.split(split[1], "h");
                long nbBlueCrits = Long.parseLong(blueSplit[0]);
                long nbRedCrits = Long.parseLong(redSplit[0]);
                for (Entry<String, Map<String, Long>> entry4 : entry3.getValue().entrySet()) {
                    String[] splitBlue = entry4.getKey().split(":");
                    List<Long> blueSuccess = mapper.readValue(splitBlue[0], listIntTypeRef);
                    long maxBlueSuccess = Long.parseLong(splitBlue[1]);
                    for (Entry<String, Long> entry5 : entry4.getValue().entrySet()) {
                        String[] splitRed = entry5.getKey().split(":");
                        List<Long> redSuccess = mapper.readValue(splitRed[0], listIntTypeRef);
                        long maxRedSuccess = Long.parseLong(splitRed[1]);
                        for (long rawThrow = 1; rawThrow <= dicesFaces; rawThrow++) {
                            long newThrow = addDiceToRed ?
                                                   (redAttribute > 20 ? rawThrow + redAttribute - 20 : rawThrow) :
                                                   (blueAttribute > 20 ? rawThrow + blueAttribute - 20 : rawThrow);

                            if (addDiceToRed) {
                                boolean isCrit = redAttribute < 20 && newThrow == redAttribute || redAttribute >= 20 && newThrow >= 20;
                                long newMaxRed = Math.max(maxRedSuccess, newThrow);
                                if (isCrit) {
                                    // new crit
                                    if (nbBlueCrits == 0) {
                                        // this is a real new crit
                                        addFor(bakedResult,
                                               nbBlueCrits, nbRedCrits + 1, new ArrayList<>(), redSuccess, maxBlueSuccess, maxRedSuccess,
                                               entry5.getValue());
                                    } else {
                                        // we just cancelled the crit, store it like each player made 1 crit to avoid useless keys
                                        addFor(bakedResult,
                                               1, 1, new ArrayList<>(), new ArrayList<>(), maxBlueSuccess, maxRedSuccess, entry5.getValue());
                                    }
                                } else if (newThrow < redAttribute) {
                                    if (nbBlueCrits == 0 && newThrow > maxBlueSuccess) {
                                        // new success beating the best of blue
                                        addFor(bakedResult,
                                               nbBlueCrits, nbRedCrits, new ArrayList<>(), addSuccess(redSuccess, newThrow), maxBlueSuccess,
                                               newMaxRed, entry5.getValue());
                                    } else {
                                        // new success but not beating red => potentially cancel some of blue results nonetheless
                                        List<Long> newBlueSuccess = new ArrayList<>(blueSuccess);
                                        newBlueSuccess.removeIf(blue -> blue <= newMaxRed);
                                        addFor(bakedResult, nbBlueCrits, nbRedCrits, newBlueSuccess, redSuccess,
                                               maxBlueSuccess, newMaxRed, entry5.getValue());
                                    }
                                } else if (newThrow > redAttribute) {
                                    // failure, does not change existing state
                                    addFor(bakedResult, nbBlueCrits, nbRedCrits, blueSuccess, redSuccess,
                                           maxBlueSuccess, maxRedSuccess, entry5.getValue());
                                } else {
                                    throw new RuntimeException("should not happen");
                                }
                            } else {
                                // new burst is blue
                                boolean isCrit = blueAttribute < 20 && newThrow == blueAttribute || blueAttribute >= 20 && newThrow >= 20;
                                long newMaxBlue = Math.max(maxBlueSuccess, newThrow);
                                if (isCrit) {
                                    // new crit
                                    if (nbRedCrits == 0) {
                                        // this is a real new crit
                                        addFor(bakedResult,
                                               nbBlueCrits + 1, nbRedCrits, blueSuccess, new ArrayList<>(), maxBlueSuccess, maxRedSuccess,
                                               entry5.getValue());
                                    } else {
                                        // we just cancelled the crit, store it like each player made 1 crit to avoid useless keys
                                        addFor(bakedResult,
                                               1, 1, new ArrayList<>(), new ArrayList<>(), maxBlueSuccess, maxRedSuccess, entry5.getValue());
                                    }
                                } else if (newThrow < blueAttribute) {
                                    if (nbRedCrits == 0 && newThrow > maxRedSuccess) {
                                        // new success beating the best of red
                                        addFor(bakedResult,
                                               nbBlueCrits, nbRedCrits, addSuccess(blueSuccess, newThrow), new ArrayList<>(), newMaxBlue,
                                               maxRedSuccess, entry5.getValue());
                                    } else {
                                        // new success but not beating red => potentially cancel some of blue results nonetheless
                                        List<Long> newRedSuccess = new ArrayList<>(redSuccess);
                                        newRedSuccess.removeIf(red -> red <= newMaxBlue);
                                        addFor(bakedResult, nbBlueCrits, nbRedCrits, blueSuccess, newRedSuccess,
                                               newMaxBlue, maxRedSuccess, entry5.getValue());
                                    }
                                } else if (newThrow > blueAttribute) {
                                    // failure, does not change existing state
                                    addFor(bakedResult, nbBlueCrits, nbRedCrits, blueSuccess, redSuccess,
                                           maxBlueSuccess, maxRedSuccess, entry5.getValue());
                                } else {
                                    throw new RuntimeException("should not happen");
                                }

                            }
                        }
                    }
                }
            }
            FileUtils.writeStringToFile(outputFile, mapper.writeValueAsString(bakedResult));
            long end = System.currentTimeMillis();
            System.out.println("computed " + outputFile.getName() + " in " + (end - start) / 1000 + "s");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Long> addSuccess(List<Long> previousSuccess, long newSuccess) {
        List<Long> res = new ArrayList<>(previousSuccess);
        res.add(newSuccess);
        res.sort(Long::compareTo);
        return res;
    }

    private static void addFor(Map<String, Map<String, Map<String, Long>>> bakedForCurrentBlueAndRedAttribute, long nbBlueCrits, long nbRedCrits,
                               List<Long> blueSuccess, List<Long> redSuccess, long maxBlueSuccess, long maxRedSuccess, long valueToAdd)
            throws JsonProcessingException {
        String key3 = String.format("c%sh%svc%sh%s", nbBlueCrits, blueSuccess.size(), nbRedCrits, redSuccess.size());
        String key4 = mapper.writeValueAsString(blueSuccess) + ":" + maxBlueSuccess;
        String key5 = mapper.writeValueAsString(redSuccess) + ":" + maxRedSuccess;
        bakedForCurrentBlueAndRedAttribute.putIfAbsent(key3, new HashMap<>());
        bakedForCurrentBlueAndRedAttribute.get(key3).putIfAbsent(key4, new HashMap<>());
        bakedForCurrentBlueAndRedAttribute.get(key3).get(key4).putIfAbsent(key5, 0L);
        bakedForCurrentBlueAndRedAttribute.get(key3).get(key4).put(key5,
                                                                   bakedForCurrentBlueAndRedAttribute.get(key3).get(key4).get(key5) + valueToAdd);
    }

    private static void aggregateAvB(long a, long b) {
        try {
            File outputFile = new File("bakedresults/" + a + "v" + b + "Final.json");
            if (outputFile.exists()) {
                System.out.println("skipping building " + outputFile.getName());
                return;
            } else {
                System.out.println("preparing " + outputFile.getName());
            }
            Map<String, Map<String, Long>> bakedFinal = new HashMap<>();
            long totalStep = maxAttributeValue * maxAttributeValue;
            long currentStep = 0;
            for (long blueAttribute = 1; blueAttribute <= maxAttributeValue; blueAttribute++) {
                for (long redAttribute = 1; redAttribute <= maxAttributeValue; redAttribute++) {
                    currentStep++;
                    System.out.print('\r');
                    System.out.print(StringUtils.leftPad(currentStep + "/" + totalStep, 20));
                    System.out.print('\r');
                    String resultCategoryKey = String.format("%sdicesat%svs%sdicesat%s", a, blueAttribute, b, redAttribute);
                    bakedFinal.putIfAbsent(resultCategoryKey, new HashMap<>());
                    Map<String, Map<String, Map<String, Long>>> baked = mapper.readValue(
                            FileUtils.readFileToString(new File(
                                    "bakedresults/temp/" + a + "v" + b + "/" + String.format("%sdicesat%sv%sdicesat%s", a, blueAttribute, b,
                                                                                                       redAttribute) + ".json")),
                            new TypeReference<HashMap<String, HashMap<String, HashMap<String, Long>>>>() {
                            });
                    for (Entry<String, Map<String, Map<String, Long>>> entry3 : baked.entrySet()) {
                        // entry3 keys: resulttype (cxhyvczhw) => we want to aggregate on this
                        long acc = 0;
                        for (Entry<String, Map<String, Long>> entry4 : entry3.getValue().entrySet()) {
                            //entry4 keys : max blue success
                            for (Entry<String, Long> entry5 : entry4.getValue().entrySet()) {
                                //entry 5 keys : max red success
                                acc += entry5.getValue();
                            }
                        }
                        String hitsKey = entry3.getKey();
                        if (hitsKey.equals("c1h0vc1h0")) {
                            hitsKey = "c0h0vc0h0";
                        }
                        bakedFinal.get(resultCategoryKey).putIfAbsent(hitsKey, 0L);
                        bakedFinal.get(resultCategoryKey).put(hitsKey, bakedFinal.get(resultCategoryKey).get(hitsKey) + acc);
                    }
                }
            }
            FileUtils.writeStringToFile(outputFile, mapper.writeValueAsString(bakedFinal));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        compute1v1();
        executor.execute(() -> aggregateAvB(1, 1));

        computeNvNfromN(1, 1, true);
        computeNvNfromN(1, 2, true);
        computeNvNfromN(1, 3, true);
        computeNvNfromN(1, 4, true);
        computeNvNfromN(1, 5, true);
        //2v2
        computeNvNfromN(1, 2, false);
        computeNvNfromN(2, 2, true);
        computeNvNfromN(2, 3, true);
        computeNvNfromN(2, 4, true);
        computeNvNfromN(2, 5, true);
        //3v3
        computeNvNfromN(2, 3, false);
        computeNvNfromN(3, 3, true);
        computeNvNfromN(3, 4, true);
        computeNvNfromN(3, 5, true);
        //4v4
        computeNvNfromN(3, 4, false);
        computeNvNfromN(4, 4, true);
        computeNvNfromN(4, 5, true);
        //5v5
        computeNvNfromN(4, 5, false);
        computeNvNfromN(5, 5, true);
        //6v6
        computeNvNfromN(5, 6, false);


        executor.execute(() -> aggregateAvB(1, 2));
        executor.execute(() -> aggregateAvB(1, 3));
        executor.execute(() -> aggregateAvB(1, 4));
        executor.execute(() -> aggregateAvB(1, 5));
        executor.execute(() -> aggregateAvB(1, 6));
        executor.execute(() -> aggregateAvB(2, 2));
        executor.execute(() -> aggregateAvB(2, 3));
        executor.execute(() -> aggregateAvB(2, 4));
        executor.execute(() -> aggregateAvB(2, 5));
        executor.execute(() -> aggregateAvB(2, 6));
        executor.execute(() -> aggregateAvB(3, 3));
        executor.execute(() -> aggregateAvB(3, 4));
        executor.execute(() -> aggregateAvB(3, 5));
        executor.execute(() -> aggregateAvB(3, 6));
        executor.execute(() -> aggregateAvB(4, 4));
        executor.execute(() -> aggregateAvB(4, 5));
        executor.execute(() -> aggregateAvB(4, 6));
        executor.execute(() -> aggregateAvB(5, 5));
        executor.execute(() -> aggregateAvB(5, 6));
        executor.execute(() -> aggregateAvB(6, 6));

    }
}

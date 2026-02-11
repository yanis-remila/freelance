
import java.util.SortedMap;
import java.util.TreeMap;


public class tariflettrevert {
    instance;
    protected SortedMap<Integer, Float>tarif;
    protected Zone om1;
    protected Zone om2;
    tariflettrevert(){
        tarif = new TreeMap<Integer, Float>();
        om1 = new Zone (nom:"OM1", tranche: 10, surtaxe: 0.02f);
        om2 = new Zone (nom:"OM2", tranche: 10, surtaxe: 0.5f);
        if (!chargerTarif(filename)){


        }
    }

}

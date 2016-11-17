package CSCI446.Project3;


/**
 * Created by thechucklingatom on 11/14/2016.
 */
public class DistanceIndex implements Comparable {
	public double distance;
	public int index;

	@Override
	public int compareTo(Object o) {
		if(o instanceof DistanceIndex){
			if(distance < ((DistanceIndex) o).distance){
				return -1;
			}else if(distance == ((DistanceIndex) o).distance){
				return 0;
			}else if(distance > ((DistanceIndex) o).distance){
				return 1;
			}
		}

		throw new ClassCastException();
	}
}

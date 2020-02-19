import java.util.*;
public class Base {
  public static void main(String args[]){
    QNode[] res = Follding.fold(new Float(args[0]), new Float(args[1]), args[2]);
    for(QNode n : res){
        System.out.print(QNode.getPos(n.x));
        System.out.print(" - ");
        System.out.println(n.H ? 'H' : 'P');
    }
  }
}

class QNode{
    int x;
    boolean H;
    QNode(int x, boolean H){
        this.x = x;
        this.H = H;
    }
    static String getPos(int x){
        return "(" + Integer.toString((x & 0xFFFF) - 0x8000) + "," + Integer.toString(((x >> 16) & 0xFFFF) - 0x8000) + ")";
    }
}

class Follding{
    static final int origin = (1 << 31) + (1 << 15),
                     eX = 1, eY = 1 << 16;

    Follding parent = null;
    int pos = 0, V = 0;
    boolean H;
    Follding(boolean H){ //root
        this.pos = origin;
        this.H   = H;
    }
    Follding(int pos, Follding parent, boolean H){
        this.parent = parent;
        this.pos = pos;
        this.H = H;
        this.V = parent.V;
        if(!H) return;
        int sum = 1;
        Follding temp = this.parent;
        if(temp != null) temp = temp.parent;
        int L = this.pos - eX, R = this.pos + eX,
            U = this.pos + eY, D = this.pos - eY;
        while(temp != null && sum < 4){
            if(temp.pos == L || temp.pos == R || temp.pos == U || temp.pos == D){
                if(temp.H) this.V --;
                sum ++;
            }
            temp = temp.parent;
        }
    }
    boolean isEmpty(int pos){
        Follding temp = this;
        while(temp != null){
            if(pos == temp.pos) return false;
            temp = temp.parent;
        }
        return true;
    }

    public static QNode[] fold(double P1, double P2, String code){
        int n = code.length();
        if(n == 0) return new QNode[]{};
        if(n == 1) return new QNode[]{new QNode(origin, code.charAt(0) == 'H')};
        if(n == 2) return new QNode[]{new QNode(origin, code.charAt(0) == 'H'), new QNode(origin + eX, code.charAt(1) == 'H')};
        int MIN = 0, i = 2, MIN_INDEX = 0;
        double AVG = 0;
        //Allways start at origin and go right
        List<Follding> Liefs = List.of(new Follding(origin + eX, new Follding(code.charAt(0) == 'H'), code.charAt(1) == 'H'));
        while( i < n ){
            boolean H = code.charAt(i) == 'H';
            List<Follding> temp = new ArrayList<Follding>();
            int count = 0, localMIN = 0, localSUM = 0;
            for(Follding F : Liefs){ //TODO make it paralel
                double rand = Math.random(); //Potentialy other implementation of random can be faster
                int L = F.pos - eX, R = F.pos + eX,
                    U = F.pos + eY, D = F.pos - eY;
                //TODO make it paralel
                Follding[] local = new Follding[]{  F.isEmpty(L) ? new Follding(L, F, H) : null,
                                                    F.isEmpty(R) ? new Follding(R, F, H) : null,
                                                    F.isEmpty(U) ? new Follding(U, F, H) : null,
                                                    F.isEmpty(D) ? new Follding(D, F, H) : null };
                for(Follding f : local) if( f != null && (f.V <= MIN || rand <= P1 || (f.V <= AVG && rand <= P2))){
                    if(localMIN > f.V){
                        localMIN  = f.V;
                        MIN_INDEX = count;
                    }
                    localSUM += f.V;
                    temp.add(f);
                    count++;
                }
            }
            if(count == 0){
                System.out.println("All paths exausted!");
                return new QNode[]{};
            }
            MIN = localMIN;
            AVG = localSUM / count;
            Liefs = temp;
            i++;
        }
        if( i < n ) return new QNode[]{}; // Something failed
        QNode[] ret = new QNode[n];
        Follding temp = Liefs.get(MIN_INDEX);
        for(i--; i >= 0; i--){
          ret[i] = new QNode(temp.pos, temp.H);
          temp = temp.parent;
        }
        return ret;
    }
}

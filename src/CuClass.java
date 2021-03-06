import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

//Yinglei worked on the desugared program (core language), in parsing, we should
//put any implementation in interface to class, and this is done in parsing, probably haven't done it yet
public abstract class CuClass {
	protected String text = "";

	String name;
	CuType            superType = new Top();
	CuContext               cTxt= new CuContext();
	List<CuExpr>        superArg;
	List<CuStat> classStatement = new ArrayList<CuStat>();
	HashMap<String, CuFun>  funList = new HashMap<String, CuFun>();
	List<String>       kindPara = null;
	Map<String, CuType> fieldTypes=new LinkedHashMap<String,CuType>();
	
	List<CuType> appliedTypePara=new ArrayList<CuType>();
	public void add(List<CuExpr> s) {}
	public void addSuper(CuType t) {}
	public void add(CuStat s) {}
	public void addFun(String v, CuTypeScheme ts, CuStat s) {}
	public void add(String v_name, CuTypeScheme ts) {}
	public void add(CuVvc v_name, CuTypeScheme ts) {}
	public boolean isInterface() {return false; }
	public CuClass calculateType(CuContext context) throws NoSuchTypeException { return this;}
	public CuContext newContext() {return null;}

	@Override public String toString() {
		return text;
	}
}

class Cls extends CuClass {
	
	public Cls(String clsintf, List<String> kc, Map<String, CuType> tc, CuContext outsideCtxt) {
		name=clsintf;
		cTxt=outsideCtxt;
		kindPara=kc;
		for (String s : kc) {cTxt.updateKind(s); }
		fieldTypes=tc;
		cTxt.mClasses.put(name, this);
	}

	//TODO: grab all the methods here
	@Override public void addSuper (CuType tt) {
		superType = tt;
		if (superType instanceof VClass){
			Map<String, CuFun> superfunLst= cTxt.mClasses.get(tt.id).funList;
			for (Map.Entry<String, CuFun> e : superfunLst.entrySet()){
				//check signature if already exists
				if (cTxt.mFunctions.containsKey(e.getKey())){
					//check signature
					e.getValue().ts.calculateType(cTxt);
					if (!e.getValue().ts.equals(cTxt.mFunctions.containsKey(e.getKey()))){
						throw new NoSuchTypeException();}
				}else{//add method if it doesn't already exist
					cTxt.updateFunction(e.getKey(),e.getValue().ts);
					funList.put(e.getKey(), e.getValue());
				}
			}
		}
		else if(superType instanceof VTypeInter) {
			for (CuType t:superType.parentType){
				Map<String, CuFun> superfunLst= cTxt.mClasses.get(t.id).funList;
				for (Map.Entry<String, CuFun> e : superfunLst.entrySet()){
					//check signature if already exists
					if (cTxt.mFunctions.containsKey(e.getKey())){
						//check signature
						e.getValue().ts.calculateType(cTxt);
						if (!e.getValue().ts.equals(cTxt.mFunctions.containsKey(e.getKey()))){
							throw new NoSuchTypeException();}
					}else{//add method if it doesn't already exist
						cTxt.updateFunction(e.getKey(),e.getValue().ts);
						funList.put(e.getKey(), e.getValue());
					}
				}
			}
		}
		else if(superType instanceof VTypePara) {
			
		}
		else if(superType instanceof Iter) {
			
		}	
		else { throw new NoSuchTypeException();}
	}
	
	@Override public void add (CuStat s) {
		classStatement.add(s);
	}
	
	@Override public void add (List<CuExpr> s) {
		superArg = s;
		//TODO: add super length type check here.
		//add mapping to context here
		//for (CuExpr ex : s){
		//	cTxt.updateMutType(name, value)
		//}
		
	}
	
	@Override public void addFun(String v, CuTypeScheme ts, CuStat s) {
		cTxt.updateFunction(v.toString(), ts);
		funList.put(v.toString(),new Function(v,ts,s));
	}
	
	@Override public CuClass calculateType(CuContext context) throws NoSuchTypeException { 
		
		return this;
	}
	
	@Override public CuContext newContext() {return cTxt;}

	/*
	@Override public String toString() {
		return String.format("class %s %s %s extends %s { %s super ( %s ) ; %s }", 
				clsintf, Helper.printList("<", kc, ">", ","), Helper.printMap("(", tc, ")", ","), superType.toString(), 
				Helper.printList("", classStatement, "", ""), Helper.printList("(", es, ")", ","), Helper.printList("", fun, "", ""));
	}
	*/
	
}

class Intf extends CuClass{
	
	public Intf (String iname, List<String> kname, CuContext outsideCtxt){
		name = iname;
		kindPara = kname;
		cTxt=outsideCtxt;
		
		//for printing
		text = "interface " + name.toString() + " <";
		for (String s : kindPara) {
			text += " " + s.toString();
		}
		text += " > extends";
	}
	@Override
	
	public void addSuper (CuType tt) throws NoSuchTypeException{
		superType = tt.calculateType(cTxt);
		if (superType instanceof VClass){
			Map<String, CuFun> superfunLst= cTxt.mClasses.get(tt.id).funList;
			for (Map.Entry<String, CuFun> e : superfunLst.entrySet()){
				//check signature if already exists
				if (cTxt.mFunctions.containsKey(e.getKey())){
					//check signature
					e.getValue().ts.calculateType(cTxt);
					if (!e.getValue().ts.equals(cTxt.mFunctions.containsKey(e.getKey()))){
						throw new NoSuchTypeException();}
				}else{//add method if it doesn't already exist
					cTxt.updateFunction(e.getKey(),e.getValue().ts);
					funList.put(e.getKey(), e.getValue());
				}
			}
		}
		else if(superType instanceof VTypeInter) {
			for (CuType t:superType.parentType){
				Map<String, CuFun> superfunLst= cTxt.mClasses.get(t.id).funList;
				for (Map.Entry<String, CuFun> e : superfunLst.entrySet()){
					//check signature if already exists
					if (cTxt.mFunctions.containsKey(e.getKey())){
						//check signature
						e.getValue().ts.calculateType(cTxt);
						if (!e.getValue().ts.equals(cTxt.mFunctions.containsKey(e.getKey()))){
							throw new NoSuchTypeException();}
					}else{//add method if it doesn't already exist
						cTxt.updateFunction(e.getKey(),e.getValue().ts);
						funList.put(e.getKey(), e.getValue());
					}
				}
			}
		}
		else if(superType instanceof VTypePara) {
			
		}
		else if(superType instanceof Iter) {
			
		}	
		else { throw new NoSuchTypeException();}
		text += " " + tt.toString();

	}
	@Override public void addFun(String v, CuTypeScheme ts, CuStat s) {
		cTxt.updateFunction(v.toString(), ts);
		funList.put(v,new Function(v,ts,s));
	}
	
	@Override public CuClass calculateType(CuContext context) throws NoSuchTypeException {
		Helper.ToDo("make sure that the type check returns its constructable component or null");
		//typeCheck();
		return this;
	}

	@Override public boolean isInterface() {return true; }
	@Override public CuContext newContext() {return cTxt;}
}




//======Class init=========

class VBoolean extends Cls {
	Boolean v=false;
	public VBoolean() {
		super("Boolean", new ArrayList<String>(), new ArrayList<String>(), new HashMap<String, CuType>(), CuContext.Empty);
		//if (val instanceof Boolean) { v=val; }
		//else { throw new NoSuchTypeException();}
	}

	public VBoolean calculateType() { return this; }
}

class VInteger extends Cls {
	Integer v=0;
	public VInteger() {
		super("Integer", new ArrayList<String>(),new ArrayList<String>(), new HashMap<String, CuType>(), CuContext.Empty);
		//if (val instanceof Integer) { v=val; }
		//else { throw new NoSuchTypeException();}
	}
	public VInteger calculateType() { return this; }
}

class VCharacter extends Cls {
	Character c;
	public VCharacter() {
		super("Character", new ArrayList<String>(),new ArrayList<String>(), new HashMap<String, CuType>(), CuContext.Empty);
		//if (val instanceof Character) { c=val; }
		//else { throw new NoSuchTypeException();}
	}
	public VCharacter calculateType() { return this; }
}

class VString extends Cls {
	String v="";
	public VString() {
		super("String", new ArrayList<String>(), new ArrayList<String>(), new HashMap<String, CuType>(), CuContext.Empty);
		//if (val instanceof String) { v=val; }
		//else { throw new NoSuchTypeException();}
	}

	public VString calculateType() { return this; }
}


class VIterable extends Cls {
	List<CuType> v;
	public VIterable(List<String> kc) {
		super("Iterable", kc, new ArrayList<String>(), new HashMap<String, CuType>(), CuContext.Empty);
		//if (val instanceof List<CuType>) { v=val; }
		//else { throw new NoSuchTypeException();}
	}
	
	public VIterable calculateType() { return this; }
}


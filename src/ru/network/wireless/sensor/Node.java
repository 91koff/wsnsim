package ru.network.wireless.sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Yakov
 * Date: 21.02.2010
 * Time: 21:27:37
 */

/**
 * todo: Список допущений в модели
 * 1. нет задержек по времени, т.е. при отправке запроса на получение адреса, узел не запускает таймер с таймаутом
 * 2. аналогично нет таймаута при поиске адреса для заимствования у соседних узлов
 * 3. события в модели происходят в последовательные кванты времени, что исключает ситуации, когда два или более узла
 * будут пытаться получить адрес у одного узла (вопрос транзакций)
 * 4. Будем считать, что все дети могут быть роутерами, т.е. CM=RM
 * 5. Если у узла a и b координаты совпадают, считаем, что это один и тот же узел.
 */

/**
 * todo: Перечисление неполноты и неточности алгоритма DIBA
 * Каждый узел, если от isConnected транслирует beacon с информацией о числе свободных мест для подключения, которые
 * он имеет. Согласно алгоритму DIBA, если кондидат на родительский узел получил REQ, но у него не будет мест, то он
 * должен инициировать сообщение AB_REQ по DIBA.
 * <p/>
 * Вопрос 1:
 * Как его могут выбрать в качестве родительского узал, если его beacon должен содержать эту информацию и новый узел
 * просто не отправит ему REQ, видя, что там 0?
 * Ответ:
 * Данный алгоритм начинает работать только тогда, когда у всех соседей заканчиваются свободные места, а узел меж тем,
 * хочет присоединиться. Т.е., пока есть свободные места - выбираем их, когда заканчиваются - начнаем брать тех, у кого
 * мест нет. Вопрос - по какому критерию выбрать из кучи узлов, у которых нет свободных мест. Может быть по расстоянию?
 * <p/>
 * Вопрос 2:
 * DIBA начинает работать, когда nwkCurChildren=max или nwkCurDepth=max. При этом согласно DIBA такой узел может
 * присодинить еще узлы. Увеличивается ли фактическая глубина дерева в случае, если у кандидата на роль родителя
 * nwkCurDepth=max? Т.е., в итоге получается, что в сети появляются узлы с глубиной, больше, чем максимальная.
 * Ответ:
 * Если у узла MaxDepth и nwkCurChildren<MaxChildren, то узел получает адрес от родителя и глубина становится больше, чем максимальная
 * (todo: проверить, что здесь нет строгой проверки на глуину)
 * Если у узла MaxDepth и nwkCurChildren=MaxChildren, то узел получает адрес от родителя своего родителя или от его детей и глубина его будет равна MaxDepth.
 * <p/>
 * Вопрос 3:
 * Узел сразу выбирает кандидата на роль родителя по числу свободных мест, а не по глубине, на которой тот находится?
 * Ответ:
 * Да, в отличие от DAAM, тут выбор происходит по числу свободны мест.
 * <p/>
 * Проблемы:
 * 1. Так как сеть имеет параметры C, R, L, то использование DIBA теоритически может означать увеличение нагрузки на
 * каждый отдельный узел как C*C в случае, если кандидат раздаст адреса всех своих соседей.
 * 2. Выбор кандидата только на основе того, у кого сколько мест может быть нерациональным (проверить). Есть предложение
 * выбирать сначала по глубине, как в DAAM, а потом уже по числу свободных мест. - не пойдет, так как на первом этапе
 * не понятно, как узнать глубину.
 */

public class Node implements Cloneable {
    /**
     * Координаты узла на плоскости
     */
    private double x;
    private double y;

    /**
     * узел включен или нет
     */
    private boolean enabled;

    /**
     * узел в сети или нет
     */
    private boolean connected;

    /**
     * узел-роутер или нет
     */
    private boolean routerCapable;

    /**
     * Адерс узла, если он в сети = isConnected()
     */
    private int address = -1;

    /**
     * родительских узел данного узла
     */
    private Node parent;

    /**
     * текущая глубина дерева, может быть только у тех, кто isConnected()
     */
    private int nwkCurDepth = -1;

    /**
     * Соседние узлы
     * neighbours = parent + routingItems
     */
    private List<Node> neighbours;

    /**
     * Список дочерних узлов первого уровня
     */
    private List<RoutingItem> routingItems;

    /**
     * @param x
     * @param y
     * @param routerCapable Конструктор по-умолчанию
     */
    public Node(double x, double y, boolean routerCapable) {
        this.x = x;
        this.y = y;
        this.routerCapable = routerCapable;
        this.neighbours = new ArrayList<Node>();
        this.routingItems = new ArrayList<RoutingItem>();
    }

//////////////////// при включении узла

    /**
     * Поиск соседних узлов в радиусе действия узла.
     * Вызывается один раз при инициализации узла в рамках модели. В случае реальной сети данная информация обновляется по таймауту updateId.
     * Каждый узел проходит в первый раз по всему массиву с координатами и находит все узлы, которые находятся в радиусе действия узла.
     * На выходе: поле neighbours содержит список соседних узлов.
     */
    public void findNeighbours(Network network) {
        for (Node node : network.getAIR()) {
            double distance = Math.sqrt(Math.pow(this.getX() - node.getX(), 2) + Math.pow(this.getY() - node.getY(), 2));
            if (distance <= network.getNodeRange() && node != this) {
                this.neighbours.add(node);
            }
        }
    }


//////////////////// методы, которые вызываются, когда узел не в сети

    /**
     * @param network Запрос на присоединение. Отправляется выбранному узлу-кандидату на роль родительского.
     *                Результат: сообщение RSP, содержащее либо адрес, либо null
     */
    public void REQ(Network network) {
        //Узел может отправить запрос только, если он включен, но не в сети
        if (this.isEnabled() && !this.isConnected()) {
            //Получаем список кандидатов на роль родительского узла из списка соседей - все, которые в сети.
            //Список состоит из пар "узел - число свободных узлов".
            //При этом при формировании числа свободных мест учитывается тип newbieNode, который входит в сеть: isRouterCapable()
            //!!!!!!не зависит от типа алгоритма!!!
            List<PossibleParentDescriptor> candidatesList = network.getAlgorithm().chooseCandidatesForParentNode(this, network);

            //Выбираем из списка кандидатов себе родительский узел, у которого будем запрашивать адрес
            //!!!!!!зависит от алгоритма!!!!
            //Node chosenParent = this.chooseParentNode(candidatesList, network);
            Node chosenParent = network.getAlgorithm().chooseParentNode(candidatesList, this, network);

            //Если найти кандидата на роль родительского узла не удалось, то выходим из метода до лучших времен.
            if (chosenParent == null) {
                //todo: сделать подсчет числа моментов, когда узел не получил адрес
                return;
            }

            //Пытаемся получить в ответе RSP от родительского узла адрес
            //!!!!!!зависит от алгоритма!!!!
            RSPMessage rspMessage = chosenParent.RSP(network, this);

            if (rspMessage != null && rspMessage.address != null) {
//                for (Integer address : Main.allAddreses.keySet()) {
//                    if (rspMessage.address.equals(address)) {
//                        //System.out.println(chosenParent + "--> newbie" + this);
//                        //System.out.println("Address " + address + " was already assigned!!!!!");
//                        int just_to_toggle_breakpoint = 0;
//                    }
//                }
//                Main.allAddreses.put(rspMessage.address, this);
//                Main.iteration++;


                //Добавляем родительскому узлу нового ребенка
                //В случае, если тут используется DIBA, то прописываем lender-а родителю, а borrower-а тому, у кого забрали адрес.

                network.getAlgorithm().addRoutingItem(rspMessage.address, chosenParent, this, rspMessage.lender);

            }
        }
    }


    /**
     * ответ от узла на запрос REQ
     */
    public RSPMessage RSP(Network network, Node newbieNode) {
        //в зависимости от алгоритма возвращаем RSPMessage: адрес и информация от кого он
        return network.getAlgorithm().getRSP(this, newbieNode, network);

    }

    public boolean hasFreeSlotFor(Node newbieNode, Network network) {
        if (newbieNode.isRouterCapable()) {
            return this.getNwkCurRoutersCount() < network.getNwkMaxRouters();
        } else {
            return this.getNwkCurEndDevicesCount() < network.getNwkMaxEndDevices();
        }
    }

    @Override
    public Object clone() {
        try {
            Node dolly = (Node)super.clone();
            dolly.neighbours = new ArrayList<Node>();
            dolly.routingItems = new ArrayList<RoutingItem>();
            return dolly;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getNwkCurChildren() {
        return routingItems.size();
    }


    public int getNwkCurDepth() {
        return nwkCurDepth;
    }

    public void setNwkCurDepth(int nwkCurDepth) {
        this.nwkCurDepth = nwkCurDepth;
    }

    public List<Node> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<Node> neighbours) {
        this.neighbours = neighbours;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRouterCapable() {
        return routerCapable;
    }

    public void setRouterCapable(boolean routerCapable) {
        this.routerCapable = routerCapable;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<RoutingItem> getRoutingItems() {
        return routingItems;
    }

    //newbie нужен, чтобы определить его тип
    //возвращает отрицательное число, если у узла есть заборовленные дети - возможно, на что-то влияет

    public Integer getFreeSlots(Network network, Node newbie) {
        if (!this.isConnected()) {
            return null;
        }
        int freeSlots = 0;
        if (newbie.isRouterCapable()) {
            freeSlots = network.getNwkMaxRouters() - this.getNwkCurRoutersCount();
        } else {
            freeSlots = network.getNwkMaxChildren() - network.getNwkMaxRouters() - this.getNwkCurEndDevicesCount();
        }
        if (freeSlots >= 0) {
            return freeSlots;
        } else {
            return 0;
        }
    }

    public int getNwkCurEndDevicesCount() {
        int nwkCurEndDevices = 0;
        for (RoutingItem routingItem : routingItems) {
            if (!routingItem.getChild().isRouterCapable()) {
                nwkCurEndDevices++;
            }
        }
        return nwkCurEndDevices;
    }

    public int getNwkCurBorrowedEndDevicesCount() {
        int nwkCurEndDevices = 0;
        for (RoutingItem routingItem : routingItems) {
            if (routingItem.isBorrowed() && !routingItem.getChild().isRouterCapable()) {
                nwkCurEndDevices++;
            }
        }
        return nwkCurEndDevices;
    }

    public int getNwkCurRoutersCount() {
        int nwkCurRouters = 0;
        for (RoutingItem routingItem : routingItems) {
            if (routingItem.getChild().isRouterCapable()) {
                nwkCurRouters++;
            }
        }
        return nwkCurRouters;
    }

    public int getNwkCurBorrowedRoutersCount() {
        int nwkCurRouters = 0;
        for (RoutingItem routingItem : routingItems) {
            if (routingItem.isBorrowed() && routingItem.getChild().isRouterCapable()) {
                nwkCurRouters++;
            }
        }
        return nwkCurRouters;
    }


    public boolean isStartingNode() {
        return this.address == 0 && this.isConnected();
    }

    /**
     * @return
     * //todo: на будущее: учесть endDevices
     * вызывается у узла lender - чтобы проверить, сколько адресов он отдал
     * не путать с методом getBorrowedRoutingItemsCount - вызывается у узла, чтобы проверить, что у него есть заборовленные адреса
     */
    public int getLendedRoutingItemsCount() {
        int borrowedRouters = 0;
        for (RoutingItem routingItem : routingItems) {
            if (routingItem.isLended()) {
                borrowedRouters++;
            }
        }
        return borrowedRouters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (Double.compare(node.x, x) != 0) return false;
        if (Double.compare(node.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = x != +0.0d ? Double.doubleToLongBits(x) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = y != +0.0d ? Double.doubleToLongBits(y) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Node{" +
                "a=" + address +
                ", d=" + nwkCurDepth +
                ", r=" + routerCapable +
                ",(x,y)=(" + x + "," + y +
                '}';
    }

    /**
     * @param network
     * @return Если 0, то узел находится на границе сети и не может больше иметь детей. В этом случае необходимо выставить параметр routerCapable=false
     *         Если > 0, то узел может присоединять дочерние узлы
     */
    public int cskip(Network network) {
        int Lm = network.getNwkMaxDepth();
        int d = this.getNwkCurDepth();
        int Rm = network.getNwkMaxRouters();
        int Cm = network.getNwkMaxChildren();
        int result = 0;
        if (Rm == 1) {
            result = 1 + Cm * (Lm - d - 1);
        } else {
            result = (int) ((1 + Cm - Rm - Cm * Math.pow(Rm, Lm - d - 1)) / (1 - Rm));
        }
        if (result < 0) {
            return 0;
        } else {
            return result;
        }
    }

    public boolean hasBorrowedAddress() {
        if (this.getParent() != null) {
            for (RoutingItem routingItem : this.getParent().getRoutingItems()) {
                if (routingItem.getChild() == this && routingItem.isBorrowed()) {
                    return true;
                }
            }
        }
        return false;
    }

}

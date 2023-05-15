package com.desert.eagle.player;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.domain.DomainService;
import com.desert.eagle.player.brokerage.BrokerageService;
import com.desert.eagle.player.commission.CommissionService;
import com.desert.eagle.player.deposit.DepositService;
import com.desert.eagle.player.ledger.LedgerService;
import com.desert.eagle.player.profit.ProfitService;
import com.desert.eagle.player.withdraw.WithdrawService;
import org.lpw.clivia.keyvalue.KeyvalueListener;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.lock.LockHelper;
import org.lpw.clivia.olcs.OlcsConfig;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.inviter.InviterService;
import org.lpw.clivia.user.online.OnlineService;
import org.lpw.photon.ctrl.upload.UploadService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.QrCode;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service(PlayerModel.NAME + ".service")
public class PlayerServiceImpl implements PlayerService, OlcsConfig, KeyvalueListener, HourJob, MinuteJob {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Numeric numeric;
    @Inject
    private DateTime dateTime;
    @Inject
    private Context context;
    @Inject
    private QrCode qrCode;
    @Inject
    private Json json;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private UploadService uploadService;
    @Inject
    private Pagination pagination;
    @Inject
    private LockHelper lockHelper;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private OnlineService onlineService;
    @Inject
    private InviterService inviterService;
    @Inject
    private ProfitService profitService;
    @Inject
    private LedgerService ledgerService;
    @Inject
    private Robot robot;
    @Inject
    private DepositService depositService;
    @Inject
    private WithdrawService withdrawService;
    @Inject
    private DomainService domainService;
    @Inject
    private CommissionService commissionService;
    @Inject
    private BrokerageService brokerageService;
    @Inject
    private PlayerDao playerDao;
    private final Map<String, PlayerModel> map = new ConcurrentHashMap<>();

    @Override
    public JSONObject query(boolean junior, String uid, String nick, String memo, int ban, String time, Date date) {
        Set<String> ids = userService.ids(null, null, null, nick, null, null, null, null, null);
        Map<String, Integer> brokerages = new HashMap<>();
        if (date != null) {
            brokerages.putAll(brokerageService.query(date));
            if (brokerages.isEmpty())
                brokerages.put("", 0);
            if (validator.isEmpty(ids))
                ids = brokerages.keySet();
            else {
                ids.retainAll(brokerages.keySet());
                if (ids.isEmpty())
                    ids.add("");
            }
        }

        return playerDao.query(ids, uid, memo, ban, time, junior, pagination.getPageSize(20), pagination.getPageNum()).toJson((player, object) -> {
            object.put("user", get(player.getId(), player));
            if (validator.isId(player.getInvitor())) {
                PlayerModel invitor = findById(player.getInvitor());
                object.put("invitor", invitor == null ? "" : invitor.getUid());
            }
            object.put("brokerage", brokerages.getOrDefault(player.getId(), 0));
        });
    }

    @Override
    public Set<String> subids(String invitorUid) {
        Set<String> set = new HashSet<>();
        PlayerModel invitor = playerDao.find(invitorUid);
        if (invitor != null)
            playerDao.query(invitor.getId(), 0, 0).getList().forEach(player -> set.add(player.getId()));

        return set;
    }

    @Override
    public JSONObject subquery(String id) {
        return playerDao.query(id, pagination.getPageSize(20), pagination.getPageNum()).toJson((player, object) -> object.put("user", get(player.getId(), player)));
    }

    @Override
    public void inviter(String code) {
        PlayerModel player = playerDao.find(code);
        if (player == null) return;

        UserModel user = userService.findById(player.getId());
        if (user != null) inviterService.set(user.getCode());
    }

    @Override
    public JSONObject sign() {
        JSONObject object = userService.sign();
        if (!json.has(object, "grade", "0") || !object.containsKey("id")) return new JSONObject();

        String id = object.getString("id");
        PlayerModel player = findById(id);
        if (player == null) {
            player = new PlayerModel();
            player.setId(id);
            PlayerModel max = playerDao.maxUid();
            int uid = max == null ? 10000 : numeric.toInt(max.getUid());
            for (int i = 1; i < 1024; i++) {
                player.setUid(numeric.toString(uid + i));
                if (playerDao.find(player.getUid()) == null) break;
            }
            if (object.containsKey("inviter")) {
                player.setInvitor(object.getString("inviter"));
//                    PlayerModel invitor = findById(player.getInvitor());
//                    if (invitor != null) {
//                        invitor.setJunior(playerDao.junior(player.getInvitor()) + 1);
//                        playerDao.save(invitor);
//                    }
            }
            player.setTime(dateTime.now());
            playerDao.insert(player);
        } else if (player.getBan() == 1) {
            object.put("id", player.getId());
            object.put("ban", 1);

            return object;
        }
        object.putAll(modelHelper.toJson(player));

        return object;
    }

    @Override
    public PlayerModel findById(String id) {
        return map.computeIfAbsent(id, key -> playerDao.findById(id));
    }

    @Override
    public JSONObject get(String id) {
        return get(id, null);
    }

    private JSONObject get(String id, PlayerModel player) {
        UserModel user = userService.findById(id);
        if (user == null) return new JSONObject();

        JSONObject object = modelHelper.toJson(user);
        if (player == null) player = findById(id);
        if (player != null) object.putAll(modelHelper.toJson(player));

        return object;
    }

    @Override
    public PlayerModel find(String uid) {
        return playerDao.find(uid);
    }

    @Override
    public JSONObject getNickAvatar(String id) {
        JSONObject object = new JSONObject();
        UserModel user = userService.findById(id);
        if (user == null) {
            String[] na = robot.getNickAvatar(id);
            if (na != null && na.length == 2) {
                object.put("nick", na[0]);
                object.put("avatar", na[1]);
            }
        } else {
            object.put("nick", user.getNick());
            object.put("avatar", user.getAvatar());
        }

        return object;
    }

    @Override
    public JSONArray junior() {
        return modelHelper.toJson(playerDao.query(userService.id(), 0, 0).getList(), (player, object) -> object.put("user", get(player.getId(), player)));
    }

    @Override
    public boolean deposit(String id, int amount) {
        PlayerModel player = findById(id);
        if (player == null) return false;


        if (amount < 0)
            return withdrawService.save(player, -amount);

        if (amount > 0)
            depositService.save(player, false, amount * 100);

        return true;
    }

    @Override
    public void gift(String id, int amount) {
        depositService.save(findById(id), true, amount * 100);
    }

    @Override
    public void memo(String id, String memo) {
        PlayerModel player = findById(id);
        if (player == null) return;

        player.setMemo(memo);
        playerDao.save(player);
    }

    @Override
    public void deposit(String id, String type, int amount, String memo) {
        collect(id, amount, type, memo);
        profitService.deposit(id, amount);
    }

    @Override
    public boolean withdraw(String id, int amount, String memo) {
        if (!disburse(id, amount, "提现", memo)) return false;

        profitService.withdraw(id, amount);

        return true;
    }

    @Override
    public boolean bet(String id, int amount, String memo) {
        return disburse(id, amount, "投注", memo);
    }

    @Override
    public void unbet(String id, int amount, String memo) {
        collect(id, amount, "撤单", memo);
    }

    @Override
    public void win(String id, int amount, String memo) {
        collect(id, amount, "中奖", memo);
    }

    @Override
    public void collect(String id, int amount, String type, String memo) {
        lockHelper.lock(lock(id), 5000, 5, key -> {
            PlayerModel player = findById(id);
            if (player == null) return null;

            int balance0 = player.getBalance();
            player.setBalance(player.getBalance() + amount);
            playerDao.save(player);
            ledgerService.save(id, type, balance0, amount, player.getBalance(), memo);

            return null;
        });
    }

    private boolean disburse(String id, int amount, String type, String memo) {
        return lockHelper.lock(lock(id), 5000, 5, key -> {
            PlayerModel player = findById(id);
            if (player == null || player.getBalance() < amount) return false;

            int balance0 = player.getBalance();
            player.setBalance(player.getBalance() - amount);
            playerDao.save(player);
            ledgerService.save(id, type, balance0, amount, player.getBalance(), memo);

            return true;
        });
    }

    private String lock(String id) {
        return PlayerModel.NAME + ":" + id;
    }

    @Override
    public JSONObject qr(String host) {
        JSONObject object = new JSONObject();
        PlayerModel player = findById(userService.id());
        if (player == null) return object;

        String qrcode = domainService.invite(player.getUid());
        if (qrcode.equals(player.getQrcode())) {
            object.put("qrcode", qrcode);
            object.put("qruri", player.getQruri());

            return object;
        }

        String path = uploadService.newSavePath("image/png", "", ".png");
        String logo = keyvalueService.value("setting.home.qrcode-logo");
        if (!validator.isEmpty(logo)) logo = context.getAbsolutePath(logo);
        qrCode.create(qrcode, 256, logo, context.getAbsolutePath(path));
        object.put("qrcode", qrcode);
        object.put("qruri", path);

        lockHelper.lock(lock(player.getId()), 5000, 5, key -> {
            PlayerModel model = findById(player.getId());
            model.setQrcode(qrcode);
            model.setQruri(path);
            playerDao.save(model);

            return null;
        });

        return object;
    }

    @Override
    public void save(String mobile) {
        if (!validator.isEmpty(mobile) && validator.isEmpty(userService.fromSession().getMobile()))
            userService.mobile(mobile);
    }

    @Override
    public void ip(String ip) {
        String id = userService.id();
        lockHelper.lock(lock(id), 5000, 5, key -> {
            PlayerModel model = findById(id);
            model.setLogin(dateTime.now());
            model.setIp(ip);
            playerDao.save(model);

            return null;
        });
    }

    @Override
    public void ban(String id, int ban) {
        lockHelper.lock(lock(id), 5000, 5, key -> {
            PlayerModel model = findById(id);
            model.setBan(ban);
            playerDao.save(model);

            return null;
        });
    }

    @Override
    public boolean noEnough(String id, int amount) {
        PlayerModel player = findById(id);

        return player == null || player.getBalance() < amount;
    }

    @Override
    public void setBetProfit(String id, int bet, int profit) {
        PlayerModel player = findById(id);
        if (player == null) return;

        lockHelper.lock(lock(id), 5000, 5, key -> {
            player.setBet(bet);
            player.setProfit(profit);
            playerDao.save(player);

            return null;
        });
    }

    @Override
    public int balance() {
        return playerDao.balance();
    }

    @Override
    public int register(Timestamp start, Timestamp end) {
        return playerDao.count(start, end);
    }

    @Override
    public int balance(String id) {
        PlayerModel player = findById(id);

        return player == null ? 0 : player.getBalance();
    }

    @Override
    public void transfer() {
        String id = userService.id();
        lockHelper.lock(lock(id), 5000, 5, key -> {
            PlayerModel player = findById(id);
            if (player == null || player.getCommissionBalance() <= 0) return null;

            int amount = player.getCommissionBalance();
            player.setCommissionBalance(0);
            playerDao.save(player);
//            commissionService.save(id, amount);
            depositService.submit("佣金转余额", amount);

            return null;
        });
    }

    @Override
    public void transfer(String id, int amount, boolean pass) {
        lockHelper.lock(lock(id), 5000, 5, key -> {
            PlayerModel player = findById(id);
            if (player == null) return null;

            if (pass) {
                int balance0 = player.getBalance();
                player.setBalance(player.getBalance() + amount);
                playerDao.save(player);
                ledgerService.save(id, "佣金转余额", balance0, amount, player.getBalance(), "");
            } else {
                player.setCommissionBalance(player.getCommissionBalance() + amount);
                playerDao.save(player);
            }

            return null;
        });
    }

    @Override
    public Set<String> ids(String uid, String nick, String invitorUid) {
        Set<String> set = new HashSet<>();
        if (!validator.isEmpty(uid)) {
            PlayerModel player = playerDao.find(uid);
            if (player == null) {
                set.add("");

                return set;
            }

            set.add(player.getId());
        }
        if (!validator.isEmpty(nick)) {
            Set<String> s = userService.ids(null, null, null, nick, null, null, null, null, null);
            if (s.isEmpty()) {
                set.clear();
                set.add("");

                return set;
            }

            if (set.isEmpty()) {
                set.addAll(s);
            } else {
                set.retainAll(s);
                if (set.isEmpty()) {
                    set.add("");

                    return set;
                }
            }
        }
        if (!validator.isEmpty(invitorUid)) {
            PlayerModel invitor = find(invitorUid);
            if (invitor == null) {
                set.clear();
                set.add("");

                return set;
            }

            Set<String> s = new HashSet<>();
            playerDao.query(invitor.getId()).getList().forEach(player -> s.add(player.getId()));
            if (s.isEmpty()) {
                set.clear();
                set.add("");

                return set;
            }

            if (set.isEmpty()) {
                set.addAll(s);
            } else {
                set.retainAll(s);
                if (set.isEmpty()) {
                    set.add("");

                    return set;
                }
            }
        }

        return set;
    }

    @Override
    public void commission(Map<String, Integer> map) {
        Map<String, Integer> invitors = new HashMap<>();
        map.forEach((id, commission) -> {
            lockHelper.lock(lock(id), 5000, 5, key -> {
                PlayerModel player = findById(id);
                if (player == null) return null;

                player.setCommissionGenerate(player.getCommissionGenerate() + commission);
                playerDao.save(player);
                if (validator.isId(player.getInvitor()))
                    invitors.put(player.getInvitor(), invitors.getOrDefault(player.getInvitor(), 0) + commission);

                return null;
            });
        });
        invitors.forEach((id, commission) -> {
            lockHelper.lock(lock(id), 5000, 5, key -> {
                PlayerModel player = findById(id);
                if (player == null) return null;

                player.setCommission(player.getCommission() + commission);
                player.setCommissionBalance(player.getCommissionBalance() + commission);
                playerDao.save(player);
                brokerageService.save(id, commission);

                return null;
            });
        });
    }

    @Override
    public void invite(String id, String invitor) {
        lockHelper.lock(lock(id), 5000L, 5, key -> {
            PlayerModel player = findById(id);
            if (player == null) return null;

            if (validator.isId(player.getInvitor())) {
                lockHelper.lock(lock(player.getInvitor()), 5000L, 5, k -> {
                    PlayerModel model = findById(player.getInvitor());
                    model.setJunior(model.getJunior() - 1);
                    playerDao.save(model);

                    return null;
                });
            }

            if (validator.isEmpty(invitor)) player.setInvitor("");
            else {
                PlayerModel model = find(invitor);
                if (model == null || model.getId().equals(id)) return null;

                player.setInvitor(model.getId());
                lockHelper.lock(lock(model.getId()), 5000L, 5, k -> {
                    PlayerModel m = findById(model.getId());
                    m.setJunior(m.getJunior() + 1);
                    playerDao.save(m);

                    return null;
                });
            }
            playerDao.save(player);

            return null;
        });
    }

    @Override
    public boolean hasInvitor(String id) {
        PlayerModel player = findById(id);

        return player != null && validator.isId(player.getInvitor());
    }

    @Override
    public void fill(UserModel user, JSONObject object) {
        PlayerModel player = findById(user.getId());
        if (player != null) {
            object.put("uid", player.getUid());
            if (!validator.isEmpty(player.getMemo())) object.put("memo", player.getMemo());
        }
    }

    @Override
    public int overdue() {
        return keyvalueService.valueAsInt("setting.data.overdue", 7);
    }

    @Override
    public void keyvalueModify(Map<String, String> map) {
        if (map.containsKey("setting.home.qrcode-logo")) playerDao.clearQrcode();
    }

    @Override
    public void keyvalueDelete(Map<String, String> map) {
    }

    @Override
    public void executeHourJob() {
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 0) {
            playerDao.zero();
            map.values().forEach(player -> {
                player.setBet(0);
                player.setProfit(0);
            });
        }
    }

    @Override
    public void executeMinuteJob() {
        playerDao.countInvite().forEach(list -> {
            String id = (String) list.get(0);
            if (!validator.isId(id)) return;

            lockHelper.lock(lock(id), 5000, 5, key -> {
                PlayerModel player = findById(id);
                if (player == null) return null;

                player.setJunior(numeric.toInt(list.get(1)));
                playerDao.save(player);

                return null;
            });
        });
    }
}

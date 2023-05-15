package com.desert.eagle.rate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.GameModel;
import com.desert.eagle.game.GameService;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service(RateModel.NAME + ".service")
public class RateServiceImpl implements RateService {
    @Inject
    private Validator validator;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Numeric numeric;
    @Inject
    private Pagination pagination;
    @Inject
    private GameService gameService;
    @Inject
    private RateDao rateDao;
    private final String[] sc09 = {"冠军", "亚军", "第三名", "第四名", "第五名", "第六名", "第七名", "第八名", "第九名", "第十名"};
    private final String[] wu15 = {"第一球", "第二球", "第三球", "第四球", "第五球"};
    private final String[] dxdslh = {"大", "小", "单", "双", "龙", "虎"};

    @Override
    public JSONObject query(String game, String type) {
        return rateDao.query(game, type, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public RateModel find(String game, String type, String name) {
        return rateDao.find(game, type, name);
    }

    @Override
    public JSONObject list(String game) {
        Map<String, JSONArray> map = new HashMap<>();
        rateDao.query(game, null, 0, 0).getList().forEach(rate -> {
            JSONObject object = new JSONObject();
            object.put("name", rate.getName());
            object.put("rate", numeric.toString(rate.getAmount() * 0.01, "0.##"));
            object.put("max", rate.getMax());
            map.computeIfAbsent(rate.getType(), key -> new JSONArray()).add(object);
        });
        JSONObject object = new JSONObject();
        object.putAll(map);

        return object;
    }

    @Override
    public void save(RateModel rate) {
        RateModel model = validator.isId(rate.getId()) ? rateDao.findById(rate.getId()) : null;
        if (model == null)
            rate.setId(null);
        rateDao.save(rate);
    }

    @Override
    public void reset(String gid) {
        GameModel game = gameService.get(gid);
        if (game == null || game.getType() > 11)
            return;

        String name = switch (game.getType()) {
            case 0, 3 -> "pc20";
            case 1, 4 -> "pc28";
            case 2, 5 -> "pc32";
            case 6, 7, 8, 9 -> "sc";
            case 10, 11 -> "wu";
            default -> null;
        };
        if (name == null)
            return;

        if (game.getType() <= 5)
            pc(gid, name);
        else if (game.getType() <= 9)
            sc(gid);
        else
            wu(gid);
    }

    private void pc(String game, String name) {
        JSONArray array = read(name);
        if (array == null)
            return;

        rateDao.delete(game);
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            String type = object.getString("name");
            JSONArray items = object.getJSONArray("items");
            for (int j = 0, s = items.size(); j < s; j++) {
                JSONObject item = items.getJSONObject(j);
                RateModel rate = new RateModel();
                rate.setGame(game);
                rate.setType(type);
                rate.setSort(j + 1);
                rate.setName(item.getString("name"));
                rate.setAmount(item.getIntValue("amount"));
                rate.setMemo(item.getString("memo"));
                rateDao.save(rate);
            }
        }
    }

    private JSONArray read(String name) {
        return json.toArray(io.readAsString(getClass().getResourceAsStream(name + ".json")));
    }

    private void sc(String game) {
        rateDao.delete(game);
        for (int i = 0; i < sc09.length; i++) {
            String type = sc09[i];
            for (int j = 1; j <= 10; j++) {
                RateModel rate = new RateModel();
                rate.setGame(game);
                rate.setType(type);
                rate.setSort(j);
                rate.setName("" + j);
                rate.setAmount(990);
                rateDao.save(rate);
            }
            for (int j = 0; j < dxdslh.length; j++) {
                if (i >= 5 && j >= 4)
                    continue;

                RateModel rate = new RateModel();
                rate.setGame(game);
                rate.setType(type);
                rate.setSort(11 + j);
                rate.setName(dxdslh[j]);
                rate.setAmount(198);
                rateDao.save(rate);
            }
        }
        String type = "冠亚和";
        for (int i = 3; i <= 19; i++) {
            RateModel rate = new RateModel();
            rate.setGame(game);
            rate.setType(type);
            rate.setSort(i - 2);
            rate.setName("" + i);
            rate.setAmount(switch (i) {
                case 3, 4, 18, 19 -> 4100;
                case 5, 6, 16, 17 -> 2000;
                case 7, 8, 14, 15 -> 1300;
                case 9, 10, 12, 13 -> 900;
                case 11 -> 700;
                default -> 0;
            });
            rateDao.save(rate);
        }
        for (int j = 0; j < 4; j++) {
            RateModel rate = new RateModel();
            rate.setGame(game);
            rate.setType(type);
            rate.setSort(18 + j);
            rate.setName(dxdslh[j]);
            rate.setAmount(j == 0 || j == 3 ? 210 : 170);
            rateDao.save(rate);
        }
    }

    private void wu(String game) {
        rateDao.delete(game);
        for (String type : wu15) {
            for (int j = 0; j <= 9; j++) {
                RateModel rate = new RateModel();
                rate.setGame(game);
                rate.setType(type);
                rate.setSort(j + 1);
                rate.setName("" + j);
                rate.setAmount(990);
                rateDao.save(rate);
            }
            for (int j = 0; j < 4; j++) {
                RateModel rate = new RateModel();
                rate.setGame(game);
                rate.setType(type);
                rate.setSort(11 + j);
                rate.setName(dxdslh[j]);
                rate.setAmount(198);
                rateDao.save(rate);
            }
        }
        String type = "总和";
        for (int j = 0; j < dxdslh.length; j++) {
            RateModel rate = new RateModel();
            rate.setGame(game);
            rate.setType(type);
            rate.setSort(j + 1);
            rate.setName(dxdslh[j]);
            rate.setAmount(198);
            rateDao.save(rate);
        }
        RateModel rate = new RateModel();
        rate.setGame(game);
        rate.setType(type);
        rate.setSort(7);
        rate.setName("和");
        rate.setAmount(900);
        rateDao.save(rate);
    }

    @Override
    public void saiChe(String gid, int haoMa, int shuangMian) {
        GameModel game = gameService.get(gid);
        if (game == null)
            return;

        if (game.getType() >= 6 && game.getType() <= 9)
            saiChe69(gid, haoMa, shuangMian);
        else if (game.getType() == 10)
            aoZhou(gid, haoMa, shuangMian);
    }

    private void saiChe69(String gid, int haoMa, int shuangMian) {
        if (haoMa > 0) {
            for (String type : sc09) {
                for (int i = 1; i <= 10; i++) {
                    saiChe(gid, type, numeric.toString(i), i, haoMa);
                }
            }
        }

        if (shuangMian > 0) {
            for (int i = 0; i < sc09.length; i++) {
                String type = sc09[i];
                for (int j = 0; j < dxdslh.length; j++) {
                    if (i >= 5 && j >= 4)
                        break;

                    saiChe(gid, type, dxdslh[j], 11 + j, shuangMian);
                }
            }
        }
    }

    private void aoZhou(String gid, int haoMa, int shuangMian) {
        if (haoMa > 0) {
            for (String type : wu15) {
                for (int i = 0; i <= 9; i++) {
                    saiChe(gid, type, numeric.toString(i), i, haoMa);
                }
            }
        }

        if (shuangMian > 0) {
            for (String type : wu15) {
                for (int j = 0; j < 4; j++) {
                    saiChe(gid, type, dxdslh[j], 11 + j, shuangMian);
                }
            }
        }
    }

    private void saiChe(String gid, String type, String name, int sort, int amount) {
        RateModel rate = rateDao.find(gid, type, name);
        if (rate == null) {
            rate = new RateModel();
            rate.setGame(gid);
            rate.setType(type);
            rate.setName(name);
        }
        rate.setSort(sort);
        rate.setAmount(amount);
        rateDao.save(rate);
    }
}

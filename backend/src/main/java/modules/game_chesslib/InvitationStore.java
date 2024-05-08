package modules.game_chesslib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import modules.game_chesslib.custom.Invitation;

public class InvitationStore {
    static volatile InvitationStore instance;

    public static InvitationStore getInstance() {
        return instance;
    }

    private InvitationStore() {
    }

    Map<String, Invitation> map = new ConcurrentHashMap<>();

    static {
        instance = new InvitationStore();
    }

    public Invitation getInvitationById(String id) {
        return map.get(id);
    }

    public void addInvitation(Invitation invitation) {
        map.put(invitation.getId(), invitation);
    }

    public void removeInvitation(String id) {
        map.remove(id);
    }
}

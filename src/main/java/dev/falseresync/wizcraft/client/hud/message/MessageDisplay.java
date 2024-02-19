package dev.falseresync.wizcraft.client.hud.message;

import dev.falseresync.wizcraft.api.client.BetterDrawContext;
import dev.falseresync.wizcraft.api.client.HudItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.PriorityQueue;
import java.util.Queue;

@Environment(EnvType.CLIENT)
public class MessageDisplay implements HudItem {
    private final Queue<MessageRequest> messageQueue = new PriorityQueue<>((a, b) -> {
        final int diff = b.priority - a.priority;
        if (diff == 0) {
            return b.message.maxLifetime() - a.message.maxLifetime();
        } else {
            return diff;
        }
    });
    private Message currentMessage = null;
    private int currentMessagePriority = -1;
    private int currentMessageLifetime = -1;
    private final MinecraftClient client;
    private final TextRenderer textRenderer;

    public MessageDisplay(MinecraftClient client, TextRenderer textRenderer) {
        this.client = client;
        this.textRenderer = textRenderer;
    }

    @Override
    public void tick() {
        var highestPriorityRequest = messageQueue.peek();
        if (highestPriorityRequest != null) {
            if (currentMessage == null
                    || highestPriorityRequest.priority > currentMessagePriority
                    && (float) currentMessageLifetime / currentMessage.maxLifetime() > 0.75f) {
                messageQueue.poll();
                currentMessage = highestPriorityRequest.message;
                currentMessagePriority = highestPriorityRequest.priority;
                currentMessageLifetime = 0;
            }
        }

        if (currentMessage == null) return;

        currentMessageLifetime += 1;
        if (currentMessageLifetime == currentMessage.maxLifetime()) {
            currentMessage = null;
            currentMessagePriority = -1;
            currentMessageLifetime = -1;
        }
    }

    @Override
    public void render(BetterDrawContext context, float tickDelta) {
        if (currentMessage == null) return;

        context.drawCenteredTextWithShadow(textRenderer, currentMessage.text(), context.getScaledWindowWidth() / 2, 20, 0xFF_FFFFFF);
    }

    public boolean hasAny() {
        return !(currentMessage == null && messageQueue.isEmpty());
    }

    public void post(Message message) {
        messageQueue.stream()
                .filter(it -> it.message.text().equals(message.text()))
                .findAny()
                .ifPresentOrElse(it -> {
                    messageQueue.remove(it);
                    it.priority += 1;
                    if (message.important() && !it.message.important() || message.maxLifetime() > it.message.maxLifetime()) {
                        it.message = new Message(
                                message.text(),
                                (message.maxLifetime() > it.message.maxLifetime()) ? message.maxLifetime() : it.message.maxLifetime(),
                                message.important() || it.message.important());
                    }
                    messageQueue.add(it);
                }, () -> {
                    messageQueue.add(new MessageRequest(message));
                });
    }

    public void post(Text text) {
        post(Message.createDefault(text));
    }

    public void postImportant(Text text) {
        post(Message.createImportant(text));
    }

    private static class MessageRequest {
        public Message message;
        public int priority = 0;

        public MessageRequest(Message message) {
            this.message = message;
        }
    }
}

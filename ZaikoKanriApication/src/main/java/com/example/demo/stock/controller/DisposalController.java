package com.example.demo.stock.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;
import com.example.demo.stock.entity.DisposalEntity;
import com.example.demo.stock.service.DisposalService;

@Controller
public class DisposalController {

    @Autowired
    private ProductService productService;

    @Autowired
    private DisposalService disposalService; // 追加

    // 商品一覧（廃棄登録画面）
    @GetMapping("/stock/DisposalRegister")
    public String DisposalRegister(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) String janCode,
            Model model) {

        List<ProductEntity> allProducts = productService.findAll();
        List<ProductEntity> productList = allProducts;

        // JAN検索
        if (janCode != null && !janCode.isEmpty()) {
            productList = productList.stream()
                    .filter(p -> janCode.equals(p.getJanCode()))
                    .toList();
        }

        // 商品名検索
        if (productId != null) {
            productList = productList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .toList();
        }

        if (productList.isEmpty()) {
            model.addAttribute("message", "一致する商品がありません");
        }

        model.addAttribute("productList", productList);
        model.addAttribute("allProducts", allProducts); // ★追加

        return "stock/DisposalRegister";
    }

    // 廃棄数入力画面
    @GetMapping("/stock/DisposalInput/{id}")
    public String disposalInput(@PathVariable Integer id, Model model) {

        // ★削除されていない商品だけ取得
        ProductEntity product = productService.findByIdAndDeletedFalse(id);

        if (product == null) {
            // 存在しない or 論理削除済は弾く
            return "redirect:/stock/DisposalRegister";
        }

        // DisposalEntity に productId をセットしておく
        DisposalEntity disposal = new DisposalEntity();
        disposal.setProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("disposal", disposal);

        return "stock/DisposalInput";
    }
    
    // 戻る用
    @PostMapping("/stock/DisposalInput")
    public String backToInput(@ModelAttribute DisposalEntity disposal, Model model) {

        ProductEntity product = productService.findById(disposal.getProductId());

        model.addAttribute("product", product);
        model.addAttribute("disposal", disposal);

        return "stock/DisposalInput";
    }
    
    @PostMapping("/stock/DisposalConfirm")
    public String disposalConfirm(
            @ModelAttribute DisposalEntity disposal,
            Model model,
            HttpSession session) {

        // ★削除されていない商品だけ取得
        ProductEntity product =
                productService.findByIdAndDeletedFalse(disposal.getProductId());

        if (product == null) {
            model.addAttribute("error", "商品が存在しません");
            return "stock/DisposalInput";
        }

        // ★在庫チェック
        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        Integer qty = disposal.getQuantity();
        if (qty == null) qty = 0;

        if (qty > stock) {
            model.addAttribute("error", "廃棄数が在庫数を超えています");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalInput";
        }

        // ★数量1チェック
        if (qty <= 0) {
            model.addAttribute("error", "廃棄数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalInput";
        }

        // ★未来日チェック
        if (disposal.getDisposalDate() != null &&
            disposal.getDisposalDate().isAfter(LocalDate.now())) {

            model.addAttribute("error", "廃棄日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalInput";
        }

        model.addAttribute("product", product);
        model.addAttribute("disposal", disposal);

        session.setAttribute("disposalConfirm", true);

        return "stock/DisposalConfirm";
    }
    

 // 廃棄保存処理
    @PostMapping("/stock/DisposalSave")
    public String disposalSave(@ModelAttribute DisposalEntity disposal, Model model,
                                                  HttpSession session) {

        Boolean flag = (Boolean) session.getAttribute("disposalConfirm");

        if (flag == null || !flag) {
            return "redirect:/stock/DisposalRegister";
        }

        session.removeAttribute("disposalConfirm");

        // ★削除されていない商品だけ取得
        ProductEntity product =
                productService.findByIdAndDeletedFalse(disposal.getProductId());

        if (product == null) {
            model.addAttribute("error", "商品が存在しません");
            return "stock/DisposalInput";
        }

        // ★未来日チェック
        if (disposal.getDisposalDate() != null &&
            disposal.getDisposalDate().isAfter(LocalDate.now())) {

            model.addAttribute("error", "廃棄日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);

            return "stock/DisposalInput";
        }

        // ★在庫チェック
        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        Integer qty = disposal.getQuantity();
        if (qty == null) qty = 0;

        if (qty > stock) {
            model.addAttribute("error", "廃棄数が在庫数を超えています");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);

            return "stock/DisposalInput";
        }

        // ★数量チェック
        if (qty <= 0) {
            model.addAttribute("error", "廃棄数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);

            return "stock/DisposalInput";
        }

        // 登録処理
        disposalService.executeDisposal(disposal);

        return "stock/DisposalComplete";
    }
    
    @GetMapping("/stock/DisposalMenu")
    public String DisposalMenuForm() {
        return "stock/DisposalMenu";
    }
    
    // 検索画面表示
    @GetMapping("/stock/DisposalEditSearch")
    public String showEditSearch() {
        return "stock/DisposalEditSearch";
    }

    // 検索実行
    @PostMapping("/stock/DisposalEdit")
    public String editDisposal(@RequestParam("disposalId") Integer id, Model model) {

        DisposalEntity disposal = disposalService.findById(id);

        if (disposal == null) {
            model.addAttribute("errorMessage", "廃棄ID " + id + " は存在しません");
            return "stock/DisposalEditSearch";
        }

        ProductEntity product = productService.findById(disposal.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "廃棄ID " + id + " の対象商品が存在しません");
            return "stock/DisposalEditSearch";
        }

        // ★① 7日制限のみ残す
        if (disposal.getCreatedAt() != null &&
            disposal.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過しているため編集できません");
            return "stock/DisposalEditSearch";
        }

        model.addAttribute("disposal", disposal);
        model.addAttribute("product", product);

        return "stock/DisposalEdit";
    }
    
	 // 編集確認画面
    @PostMapping("/stock/DisposalEditConfirm")
    public String disposalEditConfirm(@ModelAttribute DisposalEntity disposal, Model model,
    													HttpSession session) {
    	
	    	DisposalEntity dbData = disposalService.findById(disposal.getId());
	
	    	if (dbData == null || dbData.isDeleted()) {
	    	    return "redirect:/stock/DisposalEditSearch";
	    	}

    		ProductEntity product = productService.findByIdAndDeletedFalse(disposal.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "対象商品が存在しません");
            return "stock/DisposalEdit";
        }

        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        Integer qty = disposal.getQuantity();
        if (qty == null) qty = 0;

        // ★① 未来日チェック
        if (disposal.getDisposalDate() != null &&
            disposal.getDisposalDate().isAfter(LocalDate.now())) {

            model.addAttribute("errorMessage", "廃棄日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalEdit";
        } 
        
        // ★②　在庫数チェック
        boolean result = disposalService.executeEdit(disposal);

        if (!result) {
            model.addAttribute("errorMessage", "廃棄数が在庫を超えています");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalEdit";
        }

        // ★③ 1以上チェック
        if (qty <= 0) {
            model.addAttribute("errorMessage", "廃棄数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalEdit";
        }

        model.addAttribute("disposal", disposal);
        model.addAttribute("product", product);
        session.setAttribute("disposalEditConfirm", true);

        return "stock/DisposalEditConfirm";
    }
    
    // 廃棄編集画面（入力画面に戻る）
    @GetMapping("/stock/DisposalEdit/{id}")
    public String disposalEdit(@PathVariable Integer id, Model model) {
        DisposalEntity disposal = disposalService.findById(id);

        if (disposal == null) {
            // 該当レコードがなければ検索画面に戻す
            model.addAttribute("errorMessage", "廃棄ID " + id + " は存在しません");
            return "stock/DisposalEditSearch";
        }

        // 対応する商品情報も取得
        ProductEntity product = productService.findById(disposal.getProductId());

        if (product == null) {
            // 商品情報が存在しない場合も検索画面に戻す
            model.addAttribute("errorMessage", "廃棄ID " + id + " の対象商品が存在しません");
            return "stock/DisposalEditSearch";
        }

        // 商品リストも取得しておく（編集画面で select を生成する場合に必要）
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("disposal", disposal);
        model.addAttribute("product", product);
        model.addAttribute("productList", productList);

        return "stock/DisposalEdit"; // 入力画面のHTML
    }
    
    @PostMapping("/stock/DisposalEditSave")
    public String disposalEditSave(@ModelAttribute DisposalEntity disposal, Model model,
    								                  HttpSession session) {
    	
	    	Boolean flag = (Boolean) session.getAttribute("disposalEditConfirm");
	
	    	if (flag == null || !flag) {
	    	    return "redirect:/stock/DisposalEditSearch";
	    	}
	
	    	session.removeAttribute("disposalEditConfirm");

        // ① 編集前データ取得
        DisposalEntity oldDisposal = disposalService.findById(disposal.getId());
        if (oldDisposal == null) {
            model.addAttribute("errorMessage", "廃棄ID " + disposal.getId() + " が存在しません");
            return "stock/DisposalEditConfirm";
        }

        // ★② 7日制限チェック（最終防衛）
        if (oldDisposal.getCreatedAt() != null &&
            oldDisposal.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため編集できません");
            model.addAttribute("disposal", disposal);
            return "stock/DisposalEditConfirm";
        }

        // ③ 商品取得
        ProductEntity product =
        	    productService.findByIdAndDeletedFalse(oldDisposal.getProductId());
        if (product == null) {
            model.addAttribute("errorMessage", "対象の商品が存在しません");
            model.addAttribute("disposal", disposal);
            return "stock/DisposalEditConfirm";
        }

        // ★④ 未来日チェック（最終防衛）
        if (disposal.getDisposalDate() != null &&
            disposal.getDisposalDate().isAfter(LocalDate.now())) {

            model.addAttribute("errorMessage", "廃棄日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);

            return "stock/DisposalEditConfirm";
        }
        
        // ★⑤ 追加：数量チェック（ここが重要）
        Integer qty = disposal.getQuantity();
        if (qty == null) qty = 0;

        if (qty <= 0) {
            model.addAttribute("errorMessage", "廃棄数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalEditConfirm";
        }

        // ⑥ 在庫チェック＆更新処理
        boolean result = disposalService.executeEdit(disposal);

        if (!result) {
            model.addAttribute("errorMessage", "廃棄数が在庫を超えています");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalConfirm";
        }

        // ⑦ 成功
        return "stock/DisposalEditComplete";
    }
    
    // 廃棄削除画面（検索画面）表示
    @GetMapping("/stock/DisposalDeleteSearch")
    public String showDisposalDeleteSearch() {
        return "stock/DisposalDeleteSearch";
    }

    // 廃棄削除検索実行
    @PostMapping("/stock/DisposalDeleteConfirm")
    public String disposalDeleteConfirm(@RequestParam("disposalId") Integer id, Model model,
    																  HttpSession session) {

        DisposalEntity disposal = disposalService.findById(id);
        if (disposal == null) {
            model.addAttribute("errorMessage", "廃棄ID " + id + " は存在しません");
            return "stock/DisposalDeleteSearch";
        }

        // 商品情報も取得
        ProductEntity product = productService.findById(disposal.getProductId());
        if (product == null) {
            model.addAttribute("errorMessage", "廃棄ID " + id + " の対象商品が存在しません");
            return "stock/DisposalDeleteSearch";
        }
        
     // ★追加：7日制限チェック
        if (disposal.getCreatedAt() != null &&
            disposal.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "stock/DisposalDeleteSearch";
        }


        model.addAttribute("disposal", disposal);
        model.addAttribute("product", product);
        session.setAttribute("disposalDeleteConfirm", true);

        return "stock/DisposalDeleteConfirm"; // 確認画面に遷移
    }

    // 廃棄削除実行（論理削除）
    @PostMapping("/stock/DisposalDeleteComplete")
    public String disposalDeleteComplete(@ModelAttribute DisposalEntity disposal, Model model,
    													   HttpSession session) {
    	
    		Boolean flag = (Boolean) session.getAttribute("disposalDeleteConfirm");

        if (flag == null || !flag) {
            return "redirect:/stock/DisposalDeleteSearch";
        }

        session.removeAttribute("disposalDeleteConfirm");

        DisposalEntity target = disposalService.findById(disposal.getId());
        if (target == null) {
            model.addAttribute("errorMessage", "廃棄ID " + disposal.getId() + " が存在しません");
            return "stock/DisposalDeleteSearch";
        }

        // ★最終防衛：7日制限チェック（必須）
        if (target.getCreatedAt() != null &&
            target.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "stock/DisposalDeleteSearch";
        }

        // 論理削除実行
        disposalService.executeDelete(target.getId());

        return "stock/DisposalDeleteComplete";
    }
    
 // 廃棄一覧表示（論理削除されていないものだけ）
    @GetMapping("/stock/DisposalList")
    public String showDisposalList(Model model) {

        // 論理削除されていない廃棄データのみ取得
        List<DisposalEntity> disposalList = disposalService.findAllNotDeleted();

        // 全商品リストも取得して商品名表示用に渡す
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("disposalList", disposalList);
        model.addAttribute("productList", productList);

        return "stock/DisposalList"; // 先ほど作った廃棄一覧HTML
    }
}
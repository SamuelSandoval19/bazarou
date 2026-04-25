/* ============================================
   BAZAROU - Scripts principales (jQuery)
   ============================================ */

$(function () {

    // ----- Menú de usuario -----
    $('.user-menu .user-trigger').on('click', function (e) {
        e.stopPropagation();
        $(this).closest('.user-menu').toggleClass('open');
    });
    $(document).on('click', function () {
        $('.user-menu').removeClass('open');
    });

    // ----- Modal de regateo -----
    $('.btn-regatear').on('click', function () {
        $('#modal-regateo').addClass('open');
    });
    $('.modal-close, .modal-overlay').on('click', function (e) {
        if (e.target === this) {
            $(this).closest('.modal-overlay').removeClass('open');
        }
    });

    // ----- Sugerencias de regateo (calcula -10%, -15%, -20%) -----
    $('.regateo-sugerencias button').on('click', function () {
        const precio = parseFloat($(this).data('precio'));
        const desc = parseFloat($(this).data('desc'));
        const sugerido = (precio * (1 - desc / 100)).toFixed(2);
        $('#monto-oferta').val(sugerido).trigger('focus');
    });

    // ----- Galería del detalle: click en thumbnail -----
    $('.thumb').on('click', function () {
        const src = $(this).find('img').attr('src');
        $('.main-img img').attr('src', src);
        $('.thumb').removeClass('active');
        $(this).addClass('active');
    });

    // ----- Toggle de favorito (formulario inline) -----
    $('.fav-btn').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        const $btn = $(this);
        $btn.addClass('pop');
        setTimeout(() => $btn.removeClass('pop'), 300);
        // Enviamos el form
        $btn.closest('form').trigger('submit');
    });

    // ----- Subida de imágenes con drag & drop + previsualización -----
    const $dropZone = $('#upload-zone');
    const $fileInput = $('#imagenes-input');
    const $preview = $('#preview-grid');

    $dropZone.on('click', () => $fileInput.trigger('click'));

    $dropZone.on('dragover', function (e) {
        e.preventDefault();
        $(this).addClass('dragover');
    }).on('dragleave drop', function () {
        $(this).removeClass('dragover');
    }).on('drop', function (e) {
        e.preventDefault();
        const dt = new DataTransfer();
        for (const f of e.originalEvent.dataTransfer.files) dt.items.add(f);
        $fileInput[0].files = dt.files;
        $fileInput.trigger('change');
    });

    $fileInput.on('change', function () {
        $preview.empty();
        for (const file of this.files) {
            const reader = new FileReader();
            reader.onload = (e) => {
                $preview.append(`<div class="preview-item"><img src="${e.target.result}" alt=""></div>`);
            };
            reader.readAsDataURL(file);
        }
    });

    // ----- Switches de aceptaOfertas / compraProtegida -----
    $('.switch').on('click', function () {
        const $sw = $(this);
        $sw.toggleClass('on');
        const $input = $sw.find('input');
        $input.prop('checked', $sw.hasClass('on'));
    });
    // estado inicial
    $('.switch input:checked').each(function () {
        $(this).closest('.switch').addClass('on');
    });

    // ----- Aparición progresiva de tarjetas en el grid -----
    $('.product-card').each(function (i) {
        $(this).css('animation-delay', (i * 40) + 'ms').addClass('fade-up');
    });

    // ----- Auto-focus en buscador con tecla / -----
    $(document).on('keydown', function (e) {
        if (e.key === '/' && !$(e.target).is('input, textarea')) {
            e.preventDefault();
            $('.search-bar input').trigger('focus');
        }
    });

    // ----- Formato de precio en inputs -----
    $('input[data-format="money"]').on('blur', function () {
        const v = parseFloat($(this).val());
        if (!isNaN(v)) $(this).val(v.toFixed(2));
    });

});
